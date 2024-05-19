package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.lang.Iterable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;

import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.LabelElement;

public class LtyTransactionReportController extends GenericForwardComposer {

	private Chart plot1;
	private LoyaltyProgramService ltyPrgmSevice;
	private Long userId;
	private Long prgmId;
	private LoyaltyProgram prgmObj;
	private Paging loyaltyListBottomPagingId;
	private TimeZone clientTimeZone;
	private Listbox pageSizeLbId, transListLbId, timeDurLbId, cardsetLbId,subsidiaryLbId,
			storeLbId, transactionsLbId, employeeLbId,tierLbId;
	private Textbox cardSearchBoxId;
	private Combobox exportCbId;
	private Rows transRowsId;
	private Bandbox storeBandBoxId,employeeBandBoxId;
	private Foot footerId;
	private Div datesDivId, custExport$chkDivId,cardSetDivId;
	private Label plotLblId, listLblId;
	private MyDatebox fromDateboxId, toDateboxId;
	private static final String DATE_DIFF_TYPE_DAYS = "days";
	private static final String DATE_DIFF_TYPE_MONTHS = "months";
	private static final String DATE_DIFF_TYPE_YEARS = "years";
	private String typeDiff, destCards;
	private Calendar startDate, endDate;
	private int monthsDiff;
	private Window custExport;
    private List<OrganizationStores> storeList;
	private Map<String, Object> destCardMap = null;
	private Users currentUser;
	private  String  userCurrencySymbol = "$ ";
	public static Map<String, String> MONTH_MAP = new HashMap<String, String>();
	private Map<String, String> storeNameMap = new HashMap<String, String>();
	private Map<String, String> storeSBSNameMap = new HashMap<String, String>();
	private List<Map<String, Object>> storeNumberNameMapList;

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

	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LtyTransactionReportController() {
		ltyPrgmSevice = new LoyaltyProgramService();
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		prgmId = (Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		currentUser = GetUser.getUserObj();
	}

	public void doAfterCompose(Component comp) throws Exception {

		super .doAfterCompose(comp);
		logger.debug(":: before start time in doAfterCompose compose time in millis ::"
				+ System.currentTimeMillis());
		//storeBandBoxId.setTooltiptext("Store's Subsidiary is enclosed in braces");
		clientTimeZone = (TimeZone) Sessions.getCurrent().getAttribute(
				"clientTimeZone");
		destCards = Constants.STRING_NILL;
		exportCbId.setSelectedIndex(0);
		OrganizationStoresDao organizationStoresDao =	(OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		storeList = ltyPrgmSevice.getAllStores(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());

		getDateValues();
		setTransactionTypes();
		setDefaultSubsidiaries();
		setDefaultStores();
		//setEmployeeIds();//APP-4728
		if(prgmObj.getTierEnableFlag() == 'Y'){
			tierLbId.setDisabled(false);
			setTiers();
		}else{
			tierLbId.setDisabled(true);	
		}
		if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			cardsetLbId.setDisabled(false);
			cardSetDivId.setVisible(true);
			setCardSets();
		}else {
			cardsetLbId.setDisabled(true);
			cardSetDivId.setVisible(false);
		}
		plotLblId.setValue(MyCalendar.calendarToString(startDate,
				MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)
				+ " - "
				+ MyCalendar.calendarToString(endDate,
						MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));
		listLblId.setValue(MyCalendar.calendarToString(startDate,
				MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)
				+ " - "
				+ MyCalendar.calendarToString(endDate,
						MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));

		plot1.setEngine(new LineChartEngine());
		setPlotData();

		/*redrawTransactionsCount(prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), null, null, null,null);*/
		redrawTransactionsCount(prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), null, null, null, null,null);

		int totalSize = ltyPrgmSevice.getAllTransactionsCount(userId, prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), null, null, null, null,
				null, null,null);

		loyaltyListBottomPagingId.setTotalSize(totalSize);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
				.getLabel());
		loyaltyListBottomPagingId.setPageSize(pageSize);
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.addEventListener("onPaging", this);

		destCardMap = ltyPrgmSevice.getAllDestCards(userId, prgmId);

		List<Object[]> transList = ltyPrgmSevice.getAllTransactions(userId,
				prgmId, MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null),
				loyaltyListBottomPagingId.getActivePage()
						* loyaltyListBottomPagingId.getPageSize(),
				loyaltyListBottomPagingId.getPageSize(), null, null,
				null, null,null, null,null,null);

		redrawTransList(transList);

		logger.debug(":: after start time in doAfterCompose compose time in millis ::"
				+ System.currentTimeMillis());
	}

	private void getDateValues() {
		endDate = new MyCalendar(clientTimeZone);
		endDate.set(MyCalendar.HOUR_OF_DAY, 23);
		endDate.set(MyCalendar.MINUTE, 59);
		endDate.set(MyCalendar.SECOND, 59);

		startDate = new MyCalendar(clientTimeZone);
		startDate.set(MyCalendar.HOUR_OF_DAY, 00);
		startDate.set(MyCalendar.MINUTE, 00);
		startDate.set(MyCalendar.SECOND, 00);

		if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Last 30 Days")) {
			typeDiff = DATE_DIFF_TYPE_DAYS;
			monthsDiff = 30;
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE)
					- monthsDiff);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		} else if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Last 3 Months")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff = 3;
			startDate.set(MyCalendar.MONTH,
					(endDate.get(MyCalendar.MONTH) - monthsDiff) + 1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		} else if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Last 6 Months")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff = 6;
			startDate.set(MyCalendar.MONTH,
					(endDate.get(MyCalendar.MONTH) - monthsDiff) + 1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		} else if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Last 1 Year")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff = 12;
			startDate.set(MyCalendar.MONTH,
					(endDate.get(MyCalendar.MONTH) - monthsDiff) + 1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		} else if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Custom Dates")) {
			startDate = getStartDate();
			endDate = getEndDate();

			if (startDate.get(Calendar.DATE) == endDate.get(Calendar.DATE)
					&& startDate.get(Calendar.MONTH) == endDate
							.get(Calendar.MONTH)
					&& startDate.get(Calendar.YEAR) == endDate
							.get(Calendar.YEAR)) {
				typeDiff = DATE_DIFF_TYPE_DAYS;
				return;
			}

			// endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);

			int diffDays = (int) ((endDate.getTime().getTime() - startDate
					.getTime().getTime()) / (1000 * 60 * 60 * 24));

			int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

			monthsDiff = ((endDate.get(Calendar.YEAR) * 12 + endDate
					.get(Calendar.MONTH)) - (startDate.get(Calendar.YEAR) * 12 + startDate
					.get(Calendar.MONTH))) + 1;

			if (diffDays >= maxDays) {
				if (monthsDiff > 12) {
					typeDiff = DATE_DIFF_TYPE_YEARS;
					monthsDiff = endDate.get(Calendar.YEAR)
							- startDate.get(Calendar.YEAR) + 1;
				} else {
					typeDiff = DATE_DIFF_TYPE_MONTHS;
				}
			} else {
				typeDiff = DATE_DIFF_TYPE_DAYS;
				monthsDiff = diffDays;
			}

		}else{
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));		
		}

		logger.info("str endDate 2" + startDate + " " + endDate);

		// startDateStr = MyCalendar.calendarToString(startDate, null);
		// endDateStr = MyCalendar.calendarToString(endDate, null);
	}

	public Calendar getStartDate() {

		if (fromDateboxId.getValue() != null
				&& !fromDateboxId.getValue().toString().isEmpty()) {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY,
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(
					Calendar.MINUTE,
					serverFromDateCal.get(Calendar.MINUTE)
							- tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			return serverFromDateCal;
		} else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red",
					"TOP");
			return null;
		}

	}

	public Calendar getEndDate() {

		if (toDateboxId.getValue() != null
				&& !toDateboxId.getValue().toString().isEmpty()) {
			Calendar serverToDateCal = toDateboxId.getServerValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();

			serverToDateCal.set(Calendar.HOUR_OF_DAY,
					23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(
					Calendar.MINUTE,
					59 + serverToDateCal.get(Calendar.MINUTE)
							- tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			return serverToDateCal;
		} else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red",
					"TOP");
			return null;
		}

	}

	private void setTransactionTypes() {

		Listitem li = new Listitem("Enrollment", "Enrollment");
		li.setParent(transactionsLbId);

		li = new Listitem("Loyalty Issuance", "loyaltyIssuance");
		li.setParent(transactionsLbId);

		if (!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			li = new Listitem("Gift Issuance", "giftIssuance");
			li.setParent(transactionsLbId);
		}

		li = new Listitem("Redemption", "Redemption");
		li.setParent(transactionsLbId);

		li = new Listitem("Inquiry", "Inquiry");
		li.setParent(transactionsLbId);

		li = new Listitem("Returns", "Returns");
		li.setParent(transactionsLbId);

		li = new Listitem("Store Credit", "StoreCredit");
		li.setParent(transactionsLbId);

		if (!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			li = new Listitem("Transfers", "Transfer");
			li.setParent(transactionsLbId);
		}
		
		li = new Listitem("Bonus", "Bonus");
		li.setParent(transactionsLbId);
		
		li = new Listitem("Adjustments", "Adjustment");
		li.setParent(transactionsLbId);
		
		li = new Listitem("Tier Adjustment", "Tier Adjustment");
		li.setParent(transactionsLbId);
		
		
		if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY)){
		li = new Listitem("Expiry", "Expiry");
		li.setParent(transactionsLbId);
		}
		

	}
	
	private void setDefaultSubsidiaries() {

		//List<String> ltySbsList = ltyPrgmSevice.findAllLtySubsidiaries(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC); APP-4728
				//if (ltySbsList == null || ltySbsList.size() == 0)return;
				Long orgId = ltyPrgmSevice.getOrgId(userId);
				List<OrganizationStores> sbsIdList = ltyPrgmSevice.getAllStores(orgId);
				if (sbsIdList == null || sbsIdList.size() == 0)return;
				Listitem sbsItem = null;
				String subId = null;
				for (OrganizationStores org : sbsIdList) {
					logger.info("org - sub id === "+org.getSubsidiaryId());
					if(subId == null || !subId.equals(org.getSubsidiaryId())) {
						sbsItem = new Listitem(org.getSubsidiaryName()!=null?org.getSubsidiaryName():"Subsidiary ID "+org.getSubsidiaryId(), org.getSubsidiaryId());
						sbsItem.setParent(subsidiaryLbId);
						subId = org.getSubsidiaryId();
						logger.info("subId === "+subId);
					}
				} 

	}
	
	private void setDefaultStores() {

		//List<String> ltyStoreList = ltyPrgmSevice.findAllLtyStores(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC); APP-4728
		//logger.info("loyaltystoreIdList" + ltyStoreList);
		Long orgId = ltyPrgmSevice.getOrgId(userId);
		logger.info("orgId" + orgId);
		List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
		logger.info("storeIdList" + storeIdList);
		if (storeIdList == null || storeIdList.size() == 0)return;
		/*Listitem storeItem = null;
		outer: for (String storeName : ltyStoreList) {
			for (OrganizationStores org : storeIdList) {
				if (org.getHomeStoreId().equalsIgnoreCase(storeName)) {
					storeItem = new Listitem(org.getStoreName(), storeName);
					storeItem.setParent(storeLbId);
					continue outer;
				}
			}
			storeItem = new Listitem("Store ID " + storeName, storeName);

			storeItem.setParent(storeLbId);
		}*/
		
		for (OrganizationStores store : storeIdList) {
			 Listitem li = new Listitem(store.getStoreName(),
					 store.getHomeStoreId());
			 li.setParent(storeLbId);
		 }

		
		for (OrganizationStores org : storeIdList) {
			
			
			storeNameMap.put(org.getHomeStoreId(), org.getStoreName());
			
			storeSBSNameMap.put(org.getHomeStoreId()+(org.getSubsidiaryId() != null ? 
					Constants.ADDR_COL_DELIMETER+org.getSubsidiaryId():""), org.getStoreName());
		
			logger.info("store id is "+org.getHomeStoreId());
			logger.info("subsidary id is "+org.getSubsidiaryId());
			logger.info("store Name is "+org.getStoreName());


		}
		
		
	}
	
	
	
	

	/*private void setDefaultStores() {
		
		List<Object[]> ltyStoreList = ltyPrgmSevice.findAllLtyStoresAndSbs(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC);
		logger.info("loyaltystoreIdList" + ltyStoreList);
		Long orgId = ltyPrgmSevice.getOrgId(userId);
		logger.info("orgId" + orgId);
		List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
		logger.info("storeIdList" + storeIdList);
		if (storeIdList == null || storeIdList.size() == 0)return;
		Listitem storeItem = null;
		outer: for (Object[] obj : ltyStoreList) {
		for (OrganizationStores org : storeIdList) {
			if (org.getHomeStoreId().equalsIgnoreCase(obj[0].toString()) && org.getSubsidiaryId().equalsIgnoreCase(obj[1].toString())) {
				String str = org.getStoreName()+"("+ (org.getSubsidiaryName()!= null && !org.getSubsidiaryName().trim().isEmpty() ? org.getSubsidiaryName() : "") +")";
				storeItem = new Listitem(str, (obj[0].toString()+Constants.DELIMETER_DOUBLECOLON+obj[1].toString()));
				//storeItem = new Listitem(org.getStoreName(), storeName);
                storeItem.setParent(storeLbId);
				continue outer;
			}
		}
		
		storeItem = new Listitem("Store ID " + obj[0].toString()+"(SBS ID "+obj[1].toString()+")", (obj[0].toString()+Constants.DELIMETER_DOUBLECOLON+obj[1].toString()));

		storeItem.setParent(storeLbId);
	}

}*/
			/*if (eachStore.getStoreName() != null && !eachStore.getStoreName().isEmpty()) {
				Listitem li = new Listitem(eachStore.getStoreName(),eachStore.getHomeStoreId());
				li.setParent(storeLbId);
			} else {
				Listitem li = new Listitem("Store ID "+ eachStore.getHomeStoreId(),eachStore.getHomeStoreId());
				li.setParent(storeLbId);
			}
		}*/

	

	private void setEmployeeIds() {
		Long orgId = ltyPrgmSevice.getOrgId(userId);
		logger.info("orgId" + orgId);
		List<String> employeeIdList = ltyPrgmSevice.getAllempIds(orgId);
		logger.info("employeeIdList" + employeeIdList);
		if (employeeIdList == null || employeeIdList.size() == 0)
			return;
		for (String eachempId : employeeIdList) {
			Listitem li = new Listitem(eachempId, eachempId);
			li.setParent(employeeLbId);
		}
	}

	private void setCardSets() {
		List<LoyaltyCardSet> cardsetList = ltyPrgmSevice.getCardsetList(prgmId);
		logger.info("cardsetList" + cardsetList);
		if (cardsetList == null || cardsetList.size() == 0)
			return;
		for (LoyaltyCardSet eachCardset : cardsetList) {
			Listitem li = new Listitem(eachCardset.getCardSetName(),
					eachCardset.getCardSetId());
			li.setParent(cardsetLbId);
		}
	}
	
	private void setTiers() {
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
		logger.info("tierList" + tierList);
		if (tierList == null || tierList.size() == 0)
			return;
		for (LoyaltyProgramTier eachTier : tierList) {
			Listitem li = new Listitem(eachTier.getTierType(),
					eachTier.getTierId());
			li.setParent(tierLbId);
		}
	}

	private void redrawTransactionsCount(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,
			String employeeIdStr,Long tierId) {
		MessageUtil.clearMessage();
		Components.removeAllChildren(transRowsId);
		Components.removeAllChildren(footerId);
		DecimalFormat f = new DecimalFormat("#0.00");
		int totCount = 0;
		double totAmount = 0.00;
		Double totPoints = 0.0;

		Row row = null;
		Object[] retArr = ltyPrgmSevice.getTrxSummeryByDate(userId, prgmId,startDateStr,endDateStr,subsidiaryNo,storeNo,cardsetId,employeeIdStr,tierId);
		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)) {
			/*int enrCount = ltyPrgmSevice
					.getEnrollementTrans(prgmId, startDateStr, endDateStr,
							storeNo, cardsetId, employeeIdStr,tierId);*/
			//int enrCount = ltyPrgmSevice.getEnrollementTrans(prgmId, startDateStr, endDateStr, subsidiaryNo,
							//storeNo, cardsetId, employeeIdStr,tierId);
			int enrCount = retArr != null && retArr.length>0 && retArr[0]!= null? (int)retArr[0] :0;//ltyPrgmSevice.getEnrollementTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT));
			totCount = totCount + (enrCount);
			row.appendChild(new Label(enrCount + ""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}

		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("loyaltyIssuance")) {
			//Object[] issObj = ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr, endDateStr, storeNo, cardsetId,"loyaltyIssuance", employeeIdStr,tierId);
			//Object[] issObj = ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,"loyaltyIssuance", employeeIdStr,tierId);
			int issCount = retArr != null && retArr.length>0 && retArr[1]!= null? (int)retArr[1] :0; //ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr,endDateStr,null,null,null,"loyaltyIssuance",null,null);

			row = new Row();
			row.appendChild(new Label("Loyalty Issuance"));
			totCount = totCount + (issCount);
			row.appendChild(new Label(issCount+""));
			double issAmt = retArr != null && retArr.length>0 && retArr[2]!= null? (double)retArr[2] :0;
			Long issPts = 	retArr != null && retArr.length>0 && retArr[3]!= null? (long)retArr[3] :0;
			
			totAmount = totAmount + issAmt;
			row.appendChild(new Label(issAmt == 0 ? f.format(0.00) : f.format(issAmt)));
			totPoints = totPoints + (issPts );
			row.appendChild(new Label(issPts+""));
			row.setParent(transRowsId);
			
			/*row = new Row();
			row.appendChild(new Label("Loyalty Issuance"));
			totCount = totCount+ (issObj[0] == null ? 0 : Integer.parseInt(issObj[0] + ""));
			row.appendChild(new Label(issObj[0] == null ? 0 + "" : issObj[0]+ ""));
			totAmount = totAmount+ (issObj[1] == null ? 0.00 : Double.parseDouble(issObj[1]+ ""));
			row.appendChild(new Label(issObj[1] == null ? f.format(0.00) : f.format(issObj[1])));
			totPoints = totPoints+ (issObj[2] == null ? 0 : Double.parseDouble(issObj[2]+ ""));
			row.appendChild(new Label(issObj[2] == null ? 0 + "" : new Double(issObj[2] + "").intValue() + ""));
			row.setParent(transRowsId);*/
		}

		if (!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) && (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") || transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("giftIssuance"))) {
			/*Object[] issObj = ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,"giftIssuance", employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label("Gift Issuance"));
			totCount = totCount+ (issObj[0] == null ? 0 : Integer.parseInt(issObj[0] + ""));
			row.appendChild(new Label(issObj[0] == null ? 0 + "" : issObj[0]+ ""));
			totAmount = totAmount+ (issObj[1] == null ? 0.00 : Double.parseDouble(issObj[1]+ ""));
			row.appendChild(new Label(issObj[1] == null ? f.format(0.00) : f.format(issObj[1])));
			// totPoints = totPoints + (issObj[2] == null ? 0 :
			// Double.parseDouble(issObj[2]+""));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			
			
			int gftissCount = retArr != null && retArr.length>0 && retArr[4]!= null? (int)retArr[4] :0; //ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr,endDateStr,null,null,null,"loyaltyIssuance",null,null);
			double gftissAmt = retArr != null && retArr.length>0 && retArr[5]!= null? (double)retArr[5] :0;
			
			row = new Row();
			row.appendChild(new Label("Gift Issuance"));
			totCount = totCount + (gftissCount);
			row.appendChild(new Label(gftissCount+""));
			totAmount = totAmount + (gftissAmt);
			row.appendChild(new Label(gftissAmt == 0 ? f.format(0.00) : f.format(gftissAmt)));
//			totPoints = totPoints + (issObj[2] == null ? 0 : Double.parseDouble(issObj[2]+""));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}

		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)) {
			//Object[] redObjAmt = ltyPrgmSevice.getRedemptionTransAmt(prgmId, startDateStr, endDateStr,storeNo, cardsetId, employeeIdStr,tierId);
			/*Object[] redObjAmt = ltyPrgmSevice.getRedemptionTransAmt(prgmId, startDateStr, endDateStr, subsidiaryNo ,storeNo, cardsetId, employeeIdStr,tierId);
			//Object[] redObjPts = ltyPrgmSevice.getRedemptionTransPts(prgmId, startDateStr, endDateStr,storeNo, cardsetId, employeeIdStr,tierId);
			Object[] redObjPts = ltyPrgmSevice.getRedemptionTransPts(prgmId, startDateStr, endDateStr,subsidiaryNo, storeNo, cardsetId, employeeIdStr,tierId);
			int redCount = (redObjAmt[0] == null ? 0 : Integer.parseInt(redObjAmt[0] + ""))+ (redObjPts[0] == null ? 0 : Integer.parseInt(redObjPts[0]+ ""));
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION));
			totCount = totCount + redCount;
			row.appendChild(new Label(redCount + ""));
			totAmount = totAmount
					+ (redObjAmt[1] == null ? 0.00 : Double
							.parseDouble(redObjAmt[1] + ""));
			row.appendChild(new Label(redObjAmt[1] == null ? f.format(0.00) : f
					.format(redObjAmt[1])));
			totPoints = totPoints
					+ (redObjPts[1] == null ? 0 : Double
							.parseDouble(redObjPts[1] + ""));
			row.appendChild(new Label(redObjPts[1] == null ? 0 + ""
					: new Double(redObjPts[1] + "").intValue() + ""));
			row.setParent(transRowsId);*/
			
			int redCount = retArr != null && retArr.length>0 && retArr[6]!= null? (int)retArr[6] :0;
			double redeemAmt = retArr != null && retArr.length>0 && retArr[7]!= null? (double)retArr[7] :0;
			Long redeemPts = 	retArr != null && retArr.length>0 && retArr[8]!= null? (long)retArr[8] :0;
			
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION));
			totCount = totCount + redCount;
			row.appendChild(new Label(redCount+""));
			totAmount = totAmount + redeemAmt ;//(redObjAmt[1] == null ? 0.00 : Double.parseDouble(redObjAmt[1]+""));
			row.appendChild(new Label(redeemAmt== 0 ? f.format(0.00) : f.format(redeemAmt)));
			totPoints = totPoints + redeemPts;
			row.appendChild(new Label(redeemPts +""));
			row.setParent(transRowsId);

			
		}

		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)) {
			//int inqCount = ltyPrgmSevice.getInquiryTrans(prgmId, startDateStr,endDateStr, storeNo, cardsetId, employeeIdStr,tierId);
			/*int inqCount = ltyPrgmSevice.getInquiryTrans(prgmId, startDateStr,endDateStr, subsidiaryNo, storeNo, cardsetId, employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY));
			totCount = totCount + (inqCount);
			row.appendChild(new Label(inqCount + ""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			int inqCount = retArr != null && retArr.length>0 && retArr[9]!= null? (int)retArr[9] :0;;//ltyPrgmSevice.getInquiryTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY));
			totCount = totCount + (inqCount);
			row.appendChild(new Label(inqCount+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}

		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Returns")) {
			//Object[] reversalObj = ltyPrgmSevice.getReversalTrans(prgmId, startDateStr, endDateStr,storeNo, cardsetId, employeeIdStr,tierId);
			/*Object[] issObj = ltyPrgmSevice.getIssRedReversalTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId, employeeIdStr,tierId, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL);
			row = new Row();
			//row.appendChild(new Label("Returns"));
			row.appendChild(new Label("Issuance Reversal"));
			totCount = totCount
					+ (issObj[0] == null ? 0 : Integer
							.parseInt(issObj[0] + ""));
			row.appendChild(new Label(issObj[0] == null ? 0 + ""
					: issObj[0] + ""));
			totAmount = totAmount + (issObj[1] == null ? 0.00 : Double.parseDouble(issObj[1]+""));
			row.appendChild(new Label(issObj[1] == null ? f.format(0.00) : f.format(new Double(issObj[1]+""))));
			totPoints = totPoints + (issObj[2] == null ? 0 :Double.parseDouble(issObj[2]+""));
			row.appendChild(new Label(issObj[2] == null ? 0+"" : new Double(issObj[2]+"").intValue()+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			
			
			int returnCount = retArr != null && retArr.length>0 && retArr[10]!= null? (int)retArr[10] :0;;//ltyPrgmSevice.getInquiryTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			double returnAmt = retArr != null && retArr.length>0 && retArr[11]!= null? (double)retArr[11] :0;
			Long returnPts = 	retArr != null && retArr.length>0 && retArr[12]!= null? (long)retArr[12] :0;
			
			//Object[] issReversalObj = ltyPrgmSevice.getIssRedReversalTrans(prgmId,startDateStr,endDateStr, null, null, null,null,null, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL);
			row = new Row();
			row.appendChild(new Label("Issuance Reversal"));
			totCount = totCount + (returnCount);
			row.appendChild(new Label(returnCount+""));
			totAmount = totAmount + (returnAmt);
			row.appendChild(new Label(returnAmt == 0 ? f.format(0.00) : f.format(returnAmt)));
			totPoints = totPoints + (returnPts);
			row.appendChild(new Label(returnPts+""));
			row.setParent(transRowsId);
			
			/*Object[] reversalObj = ltyPrgmSevice.getIssRedReversalTrans(prgmId, startDateStr, endDateStr, subsidiaryNo,storeNo, cardsetId, employeeIdStr,tierId, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL);
			row = new Row();
			//row.appendChild(new Label("Returns"));
			row.appendChild(new Label("Redemption Reversal"));
			totCount = totCount
					+ (reversalObj[0] == null ? 0 : Integer
							.parseInt(reversalObj[0] + ""));
			row.appendChild(new Label(reversalObj[0] == null ? 0 + ""
					: reversalObj[0] + ""));
			totAmount = totAmount + (reversalObj[1] == null ? 0.00 : Double.parseDouble(reversalObj[1]+""));
			row.appendChild(new Label(reversalObj[1] == null ? f.format(0.00) : f.format(new Double(reversalObj[1]+""))));
			totPoints = totPoints + (reversalObj[2] == null ? 0 :Double.parseDouble(reversalObj[2]+""));
			row.appendChild(new Label(reversalObj[2] == null ? 0+"" : new Double(reversalObj[2]+"").intValue()+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			
			
			int returnredCount = retArr != null && retArr.length>0 && retArr[13]!= null? (int)retArr[13] :0;;//ltyPrgmSevice.getInquiryTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			double returnredAmt = retArr != null && retArr.length>0 && retArr[14]!= null? (double)retArr[14] :0;
			Long returnredPts = 	retArr != null && retArr.length>0 && retArr[15]!= null? (long)retArr[15] :0;
			
			//Object[] redReversalObj = ltyPrgmSevice.getIssRedReversalTrans(prgmId,startDateStr,endDateStr, null, null, null,null,null, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL);
			row = new Row();
			row.appendChild(new Label("Redemption Reversal"));
			totCount = totCount + (returnredCount);
			row.appendChild(new Label(returnredCount+""));
			totAmount = totAmount + (returnredAmt);
			row.appendChild(new Label(returnredAmt == 0? f.format(0.00) : f.format(returnredAmt)+""));
			totPoints = totPoints + (returnredPts );
			row.appendChild(new Label(returnredPts+""));
			row.setParent(transRowsId);
			
		}

	   if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("StoreCredit")) {
			//Object[] storeCreditObj = ltyPrgmSevice.getStoreCreditTrans(prgmId, startDateStr, endDateStr,storeNo, cardsetId, employeeIdStr,tierId);
			/*Object[] storeCreditObj = ltyPrgmSevice.getStoreCreditTrans(prgmId, startDateStr, endDateStr, subsidiaryNo,storeNo, cardsetId, employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label("Store Credit"));
			totCount = totCount+ (storeCreditObj[0] == null ? 0 : Integer.parseInt(storeCreditObj[0] + ""));
			row.appendChild(new Label(storeCreditObj[0] == null ? 0 + "": storeCreditObj[0] + ""));
			totAmount = totAmount+ (storeCreditObj[1] == null ? 0.00 : Double.parseDouble(storeCreditObj[1] + ""));
			row.appendChild(new Label(storeCreditObj[1] == null ? f.format(0.00) : f.format(storeCreditObj[1])));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
		   row = new Row();
			row.appendChild(new Label("Store Credit"));
			totCount = totCount + ( 0 );
			row.appendChild(new Label(0+""));
			totAmount = totAmount + (0);
			row.appendChild(new Label("0"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}

		if (!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)&& (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All") || transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Transfer"))) {
			//int transferTrxcount = ltyPrgmSevice.getTransferTrans(prgmId, startDateStr, endDateStr,storeNo, cardsetId, employeeIdStr,tierId);
			/*int transferTrxcount = ltyPrgmSevice.getTransferTrans(prgmId, startDateStr, endDateStr, subsidiaryNo,storeNo, cardsetId, employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label("Transfers"));
			totCount = totCount + (transferTrxcount);
			row.appendChild(new Label(transferTrxcount + ""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			int transferCount = retArr != null && retArr.length>0 && retArr[16]!= null? (int)retArr[16] :0;//ltyPrgmSevice.getTransferTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			row = new Row();
			row.appendChild(new Label("Transfers"));
			totCount = totCount + (transferCount);
			row.appendChild(new Label(transferCount+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}
		
		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Bonus")) {
			//Object[] bonusObj = ltyPrgmSevice.getBonusTrans(prgmId,startDateStr, endDateStr, storeNo, cardsetId,employeeIdStr,tierId);
			/*Object[] bonusObj = ltyPrgmSevice.getBonusTrans(prgmId,startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label("Bonus"));
			totCount = totCount+ (bonusObj[0] == null ? 0 : Integer.parseInt(bonusObj[0] + ""));
			row.appendChild(new Label(bonusObj[0] == null ? 0 + "" : bonusObj[0]+ ""));
			totAmount = totAmount+ (bonusObj[1] == null ? 0.00 : Double.parseDouble(bonusObj[1]+ ""));
			row.appendChild(new Label(bonusObj[1] == null ? f.format(0.00) : f.format(bonusObj[1])));
			totPoints = totPoints+ (bonusObj[2] == null ? 0 : Double.parseDouble(bonusObj[2]+ ""));
			row.appendChild(new Label(bonusObj[2] == null ? 0 + "" : new Double(bonusObj[2] + "").intValue() + ""));
			row.setParent(transRowsId);*/
			
			int bonusCount = retArr != null && retArr.length>0 && retArr[17]!= null? (int)retArr[17] :0;
			double bonusAmt = retArr != null && retArr.length>0 && retArr[18]!= null? (double)retArr[18] :0;
			Long bonusPts = 	retArr != null && retArr.length>0 && retArr[19]!= null? (long)retArr[19] :0;
			
			//Object[] bonusObj = ltyPrgmSevice.getBonusTrans(prgmId,startDateStr, endDateStr, null, nul, null,null,null);
			row = new Row();
			row.appendChild(new Label("Bonus"));
			totCount = totCount+ bonusCount ;//(bonusObj[0] == null ? 0 : Integer.parseInt(bonusObj[0] + ""));
			row.appendChild(new Label(bonusCount+ ""));
			totAmount = totAmount+bonusAmt;// (bonusObj[1] == null ? 0.00 : Double.parseDouble(bonusObj[1]+ ""));
			row.appendChild(new Label(bonusAmt == 0 ? f.format(0.00) : f.format(bonusAmt)));
			totPoints = totPoints+bonusPts;// (bonusObj[2] == null ? 0 : Double.parseDouble(bonusObj[2]+ ""));
			row.appendChild(new Label(bonusPts == 0 ? 0 + "" :bonusPts + ""));
			row.setParent(transRowsId);
		}
		
		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Adjustment")) {
			//Object[] adjustObj = ltyPrgmSevice.getAdjustmentTrans(prgmId,startDateStr, endDateStr, storeNo, cardsetId,employeeIdStr,tierId);
			/*Object[] adjustObj = ltyPrgmSevice.getAdjustmentTrans(prgmId,startDateStr, endDateStr,subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
			row = new Row();
			row.appendChild(new Label("Adjustments"));
			totCount = totCount+ (adjustObj[0] == null ? 0 : Integer.parseInt(adjustObj[0] + ""));
			row.appendChild(new Label(adjustObj[0] == null ? 0 + "" : adjustObj[0]+ ""));
			totAmount = totAmount+ (adjustObj[1] == null ? 0.00 : Double.parseDouble(adjustObj[1]+ ""));
			row.appendChild(new Label(adjustObj[1] == null ? f.format(0.00) : f.format(adjustObj[1])));
			totPoints = totPoints+ (adjustObj[2] == null ? 0 : Double.parseDouble(adjustObj[2]+ ""));
			row.appendChild(new Label(adjustObj[2] == null ? 0 + "" : new Double(adjustObj[2] + "").intValue() + ""));
			row.setParent(transRowsId);*/
			int adjCount = retArr != null && retArr.length>0 && retArr[20]!= null? (int)retArr[20] :0;
			double adjAmt = retArr != null && retArr.length>0 && retArr[21]!= null? (double)retArr[21] :0;
			Long adjPts = 	retArr != null && retArr.length>0 && retArr[22]!= null? (long)retArr[22] :0;
			row = new Row();
			row.appendChild(new Label("Adjustments"));
			totCount = totCount+ (adjCount);
			row.appendChild(new Label(adjCount+ ""));
			totAmount = totAmount+adjAmt;// (adjustObj[1] == null ? 0.00 : Double.parseDouble(adjustObj[1]+ ""));
			row.appendChild(new Label(adjAmt == 0? f.format(0.00) : f.format(adjAmt)));
			totPoints = totPoints+ (adjPts);
			row.appendChild(new Label(adjPts + ""));
			row.setParent(transRowsId);

		}
		
		
		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER)) {
			
			//int changeTierCount = ltyPrgmSevice.getChangeTierTrans(prgmId,startDateStr,endDateStr,null,null,null,null);
			/*int changeTierCount = ltyPrgmSevice.getChangeTierTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			row = new Row();
			row.appendChild(new Label("Tier Adjustment"));
			totCount = totCount + (changeTierCount);
			row.appendChild(new Label(changeTierCount+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);*/
			int changeTierCount = retArr != null && retArr.length>0 && retArr[23]!= null? (int)retArr[23] :0;//ltyPrgmSevice.getChangeTierTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
			row = new Row();
			row.appendChild(new Label("Tier Adjustment"));
			totCount = totCount + (changeTierCount);
			row.appendChild(new Label(changeTierCount+""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
		}
		// Expiry Transaction//
		if (transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")|| transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY)) {
			
			int ExpiryTierCount=ltyPrgmSevice.getExpiryTierTrans1(prgmId,
					startDateStr,endDateStr, userId, OCConstants.LOYALTY_TRANS_TYPE_EXPIRY);
		
			row = new Row();
			row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY));
			totCount = totCount + (ExpiryTierCount);
			row.appendChild(new Label(ExpiryTierCount+ ""));
			row.appendChild(new Label("--"));
			row.appendChild(new Label("--"));
			row.setParent(transRowsId);
			logger.info("Expiry transaction count"+ExpiryTierCount);
		}	
		
		Footer footer = new Footer();
		footer.appendChild(new Label("TOTAL"));
		footer.setParent(footerId);

		footer = new Footer();
		footer.appendChild(new Label(totCount + ""));
		footer.setParent(footerId);

		footer = new Footer();
		footer.appendChild(new Label(f.format(totAmount)));
		footer.setParent(footerId);

		footer = new Footer();
		footer.appendChild(new Label(totPoints.intValue() + ""));
		footer.setParent(footerId);

	}

	public void setPlotData() {

		try {
			logger.debug(" >> before setPlotData time In millis ::"
					+ System.currentTimeMillis());

			String transType = null;
			if (!transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
		     transType = transactionsLbId.getSelectedItem().getValue().toString();
			}
			String storeNo = Constants.STRING_NILL;
			String subsidiaryNo = Constants.STRING_NILL;
			for (Listitem item : subsidiaryLbId.getSelectedItems()) {
				if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if (subsidiaryNo.length() == 0) {
						subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
					} else {
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
					}
				}
			}
			for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + item.getValue() + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+ item.getValue() + "'";

					}
				}
			}
			/*for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
					String storeSub = (String) item.getValue();
					String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + store[0] + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ store[0] + "'";
					}
					if(subsidiaryNo.length() == 0)
						subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
					else
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
								+ store[1] + "'";
				}

			}
			logger.info("setPlotData():::stores::"+storeNo);*/

			Long cardsetId = null;
			if (!cardsetLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem()
						.getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			for (Listitem item : employeeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'" + item.getValue()
								+ "'";
					} else {
						employeeIdStr = employeeIdStr
								+ Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			logger.debug(" startDateStr :"
					+ MyCalendar.calendarToString(startDate, null)
					+ " AND endDateStr Str ::"
					+ MyCalendar.calendarToString(endDate, null));

			CategoryModel model = new SimpleCategoryModel();
			List<Object[]> transRates = null;
			List<Object[]> returnRates = null; 
			if(transType == null){
				 //transRates = ltyPrgmSevice.findTotTransactionsRateforAll(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType,storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
				 transRates = ltyPrgmSevice.findTotTransactionsRateforAll(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType,subsidiaryNo, storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
				 //returnRates = ltyPrgmSevice.findTotTransactionsRateforReturn(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType,storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
				// returnRates = ltyPrgmSevice.findTotTransactionsRateforReturn(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType, subsidiaryNo, storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
				//logger.info(" Return list size  ="+returnRates.size()+"    data: "+returnRates);
			}else{
	            //transRates = ltyPrgmSevice.findTotTransactionsRate(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType,storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
	            transRates = ltyPrgmSevice.findTotTransactionsRate(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null), transType, subsidiaryNo, storeNo, cardsetId, typeDiff, employeeIdStr,tierId);
			}
			Map<String, Integer> transMap = null;
			if (transRates != null && transRates.size() > 0) {
				transMap = new HashMap<String, Integer>();
				for (Object[] obj : transRates) {
					transMap.put(obj[1].toString(),
							Integer.parseInt(obj[0].toString()));
				}
			}
			
			/*if (returnRates != null && returnRates.size() > 0) {
				for (Object[] obj : returnRates) {
					if(transMap.get(obj[1].toString()) == null){
						transMap.put(obj[1].toString(),	Integer.parseInt(obj[0].toString()));
							}else{
								transMap.put(obj[1].toString(),	transMap.get(obj[1].toString())+Integer.parseInt(obj[0].toString()));
							}
				}
			}
*/
			Calendar tempCal = Calendar.getInstance();
			tempCal.setTimeZone(clientTimeZone);
			tempCal.setTimeInMillis(startDate.getTimeInMillis());
			String currDate = "";

			if (typeDiff.equalsIgnoreCase(DATE_DIFF_TYPE_DAYS)) {
				plot1.setXAxis("Days");
				do {
					currDate = "" + tempCal.get(startDate.DATE);
					if (transMap != null) {
						model.setValue("No.of transactions",currDate,transMap.containsKey(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE)) ? transMap.get(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_YEARTODATE)): 0);
					} else {
						model.setValue("No.of transactions", currDate, 0);
					}
					tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
				} while (tempCal.before(endDate) || tempCal.equals(endDate));
			}

			else if (typeDiff.equals(DATE_DIFF_TYPE_MONTHS)) {
				plot1.setXAxis("Months");
				int i = 1;
				do {
					i++;

					currDate = "" + (tempCal.get(startDate.MONTH) + 1);
					if (transMap != null) {
						model.setValue(
								"No.of transactions",
								MONTH_MAP.get(currDate),
								transMap.containsKey(currDate) ? transMap
										.get(currDate) : 0);
					} else {
						model.setValue("No.of transactions",
								MONTH_MAP.get(currDate), 0);
					}

					tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

				} while (i <= monthsDiff);
			}

			else if (typeDiff.equals(DATE_DIFF_TYPE_YEARS)) {
				plot1.setXAxis("Years");
				int i = 1;
				do {
					i++;

					currDate = "" + (tempCal.get(startDate.YEAR));
					if (transMap != null) {
						model.setValue("No.of transactions", currDate, transMap
								.containsKey(currDate) ? transMap.get(currDate)
								: 0);
					} else {
						model.setValue("No.of transactions", currDate, 0);
					}
					tempCal.set(Calendar.YEAR, tempCal.get(Calendar.YEAR) + 1);

				} while (i <= monthsDiff);
			}
			plot1.setModel(model);

			logger.debug(" >> after setPlotData time In millis ::"
					+ System.currentTimeMillis());
		} catch (Exception e) {
			logger.debug(" Exception : while generating the line chart ",
					(Throwable) e);
		}
	} // setPlotData
	private void redrawTransList(List<Object[]> transList) {
	
		MessageUtil.clearMessage();
		//Long orgId = ltyPrgmSevice.getOrgId(userId);
		
		DecimalFormat f = new DecimalFormat("#0.00");
		int count = transListLbId.getItemCount();
	
		for (; count > 0; count--) {
			transListLbId.removeItemAt(count - 1);
		}
	
		//System.gc();
	
		if (transList == null)
			return;
		if (transList != null && transList.size() > 0) {
			logger.info("setting listitems");
			Listitem li;
			Listcell lc;
			for (Object[] transactionObjArr : transList) {
				// LoyaltyTransactionChild transactionObj =
				// (LoyaltyTransactionChild) transactionObjArr[0];
	
				li = new Listitem();
	
				SimpleDateFormat formatter = new SimpleDateFormat(
						MyCalendar.FORMAT_DATETIME_STYEAR);
				Date date = null;
				try {
					date = (Date) formatter.parse(transactionObjArr[10]
							.toString().trim());
				} catch (ParseException e) {
					logger.error("Exception ::", e);
				}
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				String createdDate = "";
				createdDate = MyCalendar.calendarToString(cal,
						MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
	
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(createdDate);
				lc.setParent(li);
	
				/*
				 * String memNuber = transactionObjArr[15] != null ?
				 * OCConstants.
				 * LOYALTY_TRANS_TYPE_TRANSFER.equalsIgnoreCase(transactionObjArr
				 * [3].toString())?
				 * destCardMobjArrnObjArr[15].toString()).toString
				 * ()+" (Source : "+transactionObjArr[0].toString()+")":
				 * destCardMap
				 * .get(transactionObjArr[15].toString()).toString()+" (Original : "
				 * +
				 * transactionObjArr[0].toString()+")":transactionObjArr[0].toString
				 * ();
				 */
			
				//APP-3192
			//	LoyaltyTransactionChild loyaltyTransactionChild;
				lc=new Listcell();
				if (OCConstants.LOYALTY_TRANS_TYPE_BONUS
						.equalsIgnoreCase(transactionObjArr[3].toString())) {
					String receiptNumber =transactionObjArr[14]==null || transactionObjArr[14].toString().trim().isEmpty()?"--": transactionObjArr[14].toString();
					lc.setLabel(receiptNumber);
					lc.setStyle("padding-left:10px;");
					lc.setTooltiptext(receiptNumber);
					lc.setParent(li);
				
				}else {
				String receiptNumber = transactionObjArr[25]==null || transactionObjArr[25].toString().trim().isEmpty()?"--": transactionObjArr[25].toString();
			
				lc.setLabel(receiptNumber);
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(receiptNumber);
				lc.setParent(li);
				}
				
				
				
				String memNuber = transactionObjArr[0].toString();
				if(transactionObjArr[15] != null){
					if(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER.equalsIgnoreCase(transactionObjArr[3].toString())){
						 if(destCardMap.get(transactionObjArr[15]) != null ){
							memNuber = destCardMap.get(transactionObjArr[15].toString()).toString()+ " (Source : " + transactionObjArr[0].toString() + ")";
						}
					}else{
						if(destCardMap.get(transactionObjArr[15]) != null ){
							memNuber = destCardMap.get(transactionObjArr[15].toString()).toString()+ " (Original : "+ transactionObjArr[0].toString() + ")";
						}
					}
				}else if(transactionObjArr[15] == null && OCConstants.LOYALTY_TRANS_TYPE_TRANSFER.equalsIgnoreCase(transactionObjArr[3]	.toString())){
					memNuber = ((String) transactionObjArr[16]).split("dest:")[1]+ " (Source : "+ transactionObjArr[0].toString() + ")";
				}
				else{	
					memNuber = transactionObjArr[0].toString();
				}

				
	        	   
	        	  
						
				/*String memNuber = transactionObjArr[15] != null ? OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
						.equalsIgnoreCase(transactionObjArr[3].toString()) ? destCardMap
						.get(transactionObjArr[15].toString()).toString()
						+ " (Source : " + transactionObjArr[0].toString() + ")"
						: destCardMap.get(transactionObjArr[15].toString())
								.toString()
								+ " (Original : "
								+ transactionObjArr[0].toString() + ")"
						: OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
								.equalsIgnoreCase(transactionObjArr[3]
										.toString()) ? ((String) transactionObjArr[16])
								.split("dest:")[1]
								+ " (Source : "
								+ transactionObjArr[0].toString() + ")"
								: transactionObjArr[0].toString();*/
				lc = new Listcell();
				lc.setLabel(memNuber);
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(memNuber);
				lc.setParent(li);
	
				lc = new Listcell();
				lc.setLabel(transactionObjArr[1].toString());
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(transactionObjArr[1].toString());
				lc.setParent(li);
	
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				if(transactionObjArr[22] != null){
					String sbsName = "Subsidiary ID "+transactionObjArr[22].toString() ;
				for (OrganizationStores org : storeList){
				if (transactionObjArr[22].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
					sbsName = org.getSubsidiaryName().toString();
			    	break;
				}}
					lc.setLabel(sbsName);
					lc.setTooltiptext(sbsName);
				}else{
					lc.setLabel("");
					lc.setTooltiptext("");
					}
				
				lc.setParent(li);
				
				
				
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				if(transactionObjArr[2] != null){
					
					String sName = "Store ID "+transactionObjArr[2].toString() ;
					

					for (OrganizationStores org : storeList){
						
						if (transactionObjArr[2].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {					//	sName = org.getStoreName().toString()
					
							//sName=fetchStoreName(transactionObjArr[2].toString()); 
							try {
							
							sName = transactionObjArr[22].toString() == null ?( storeNameMap.containsKey(transactionObjArr[2].toString()) ? storeNameMap.get(transactionObjArr[2].toString())
								: transactionObjArr[2].toString() == null ? "--"
										: "Store ID " + transactionObjArr[2].toString()) : 
									(storeSBSNameMap.containsKey(transactionObjArr[2].toString()+Constants.ADDR_COL_DELIMETER+transactionObjArr[22].toString() ) ? 
											storeSBSNameMap.get(transactionObjArr[2].toString()+Constants.ADDR_COL_DELIMETER+transactionObjArr[22].toString() ):  "Store ID " + transactionObjArr[2].toString() );
							}catch (Exception e) {
								logger.error("Exception ::", e);
							}
							logger.info("storeSBSNameMap.getHomeStoreId()" , storeSBSNameMap.containsKey(org.getHomeStoreId()));
							logger.info("storeSBSNameMap.getSubsidiaryName() ",storeSBSNameMap.containsKey(org.getHomeStoreId()+Constants.ADDR_COL_DELIMETER+org.getSubsidiaryName()));
							logger.info("store1  id is  "+org.getHomeStoreId());
							logger.info("subsidary1 id is "+org.getSubsidiaryId());
							logger.info("store1 Name is "+org.getStoreName());
							logger.info("storeList is "+storeList.size());

						 	break;
						}
					}

					lc.setLabel(sName);
					lc.setTooltiptext(sName);
				
				}else{
					
					lc.setLabel("");
					lc.setTooltiptext("");
				}
				
				lc.setParent(li);
				
				
				
				
				
				
				
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				if (OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE
						.equalsIgnoreCase(transactionObjArr[3].toString())) {
					if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT
							.equalsIgnoreCase(transactionObjArr[4].toString())) {
						lc.setLabel("Gift Issuance");
						lc.setTooltiptext("Gift Issuance");
					} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD
							.equalsIgnoreCase(transactionObjArr[4].toString())) {
					
						lc.setLabel("Reward");
						lc.setTooltiptext("Reward");
					
					}
					else {
						lc.setLabel("Loyalty Issuance");
						lc.setTooltiptext("Loyalty Issuance");
					}
				} else if (OCConstants.LOYALTY_TRANS_TYPE_RETURN
						.equalsIgnoreCase(transactionObjArr[3].toString())) {
					if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL
							.equalsIgnoreCase(transactionObjArr[4].toString())) {
						lc.setLabel("Issuance Reversal");
						lc.setTooltiptext("Issuance Reversal");
					} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL
							.equalsIgnoreCase(transactionObjArr[4].toString())) {
						lc.setLabel("Redemption Reversal");
						lc.setTooltiptext("Redemption Reversal");
					} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT
							.equalsIgnoreCase(transactionObjArr[4].toString())) {
						lc.setLabel("Store Credit");
						lc.setTooltiptext("Store Credit");
					}
				} else {
					lc.setLabel(transactionObjArr[3].toString());
					lc.setTooltiptext(transactionObjArr[3].toString());
				}
	
				lc.setParent(li);
	
				lc = new Listcell();
				Listcell cell=new Listcell();
				lc.setStyle("padding-left:10px;");
				cell.setStyle("padding-left:10px;");
	
				if (((transactionObjArr[13] != null && ((String) transactionObjArr[13])
						.equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) || (transactionObjArr[4] != null && transactionObjArr[4]
						.toString()
						.equalsIgnoreCase(
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)))
						&& (!((String) transactionObjArr[3])
								.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))
						&& !((String) transactionObjArr[3])
								.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)) {
					Double enteredAmt = transactionObjArr[5] == null ? 0
							: Double.parseDouble(transactionObjArr[5]
									.toString());
					String valueEntered = ((String) transactionObjArr[3])
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? (transactionObjArr[4] != null ? ((String) transactionObjArr[4])
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "
							+ enteredAmt.longValue()
							: "- " + enteredAmt.longValue()
							: "0")
							: "" + enteredAmt.longValue();
					lc.setLabel(valueEntered);
					lc.setTooltiptext(valueEntered);
					cell.setLabel("Points");
					cell.setTooltiptext("Points");
				} else {
					double enteredAmt = transactionObjArr[5] == null ? 0.0
							: Double.parseDouble(transactionObjArr[5]
									.toString());
					double excludeAmt = transactionObjArr[6] == null ? 0.0
							: Double.parseDouble(transactionObjArr[6]
									.toString());
					String valueEntered = ((String) transactionObjArr[3])
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? (transactionObjArr[4] != null ? ((String) transactionObjArr[4])
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "
							+ f.format(enteredAmt - excludeAmt)
							: "- " + f.format(enteredAmt - excludeAmt)
							: "0.00")
							: f.format(enteredAmt - excludeAmt);
					if (transactionObjArr[5] != null) {
						lc.setLabel(valueEntered);
						lc.setTooltiptext(valueEntered);
					
						try {
					
							String vcode=((String) transactionObjArr[13])
								.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) ? "Currency"
								: transactionObjArr[13].toString();
						
						cell.setLabel(vcode);
						cell.setTooltiptext(vcode);
					
						}catch(Exception e) {}
					} else if (((String) transactionObjArr[3])
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)
							|| ((String) transactionObjArr[3])
									.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_BONUS)) {
						valueEntered = ((String) transactionObjArr[13])
								.equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "0"
								: "0.00";
						String vcode=((String) transactionObjArr[13])
								.equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "Points"
								: transactionObjArr[13].toString();
					
						
						logger.info("value vcodeis"+vcode);
						lc.setLabel(valueEntered);
						lc.setTooltiptext(valueEntered);
						cell.setLabel(vcode);
						cell.setTooltiptext(vcode);
					} else {
						lc.setLabel("--");
						lc.setTooltiptext("--");
						cell.setLabel("--");
						cell.setTooltiptext("--");
					}
				}
	
				lc.setParent(li);
				cell.setParent(li);
				//amount diff
				String amountDiff="";
				if (transactionObjArr[17] != null&& !transactionObjArr[17].toString().isEmpty())
				        amountDiff=(String) transactionObjArr[17];
								else 
									amountDiff =  "0";
					
								lc = new Listcell();
								lc.setStyle("padding-left:10px;");
								lc.setLabel(amountDiff);
								lc.setTooltiptext(amountDiff);
								lc.setParent(li);
					
	           //point diff
								String pointDiff="";
								if (transactionObjArr[19] != null&& !transactionObjArr[19].toString().isEmpty())
									pointDiff=Utility.truncateToInteger((String)transactionObjArr[19]);//APP-823
												else 
													pointDiff =  "0";
									
												lc = new Listcell();
												lc.setStyle("padding-left:10px;");
												lc.setLabel(pointDiff);
												lc.setTooltiptext(pointDiff);
												lc.setParent(li);
																
				String balanceStr = "";
				if (transactionObjArr[7] != null
						&& !transactionObjArr[7].toString().isEmpty()
						&& Double.parseDouble(transactionObjArr[7].toString()) != 0.0
						&& transactionObjArr[8] != null
						&& !transactionObjArr[8].toString().isEmpty()
						&& Double.parseDouble(transactionObjArr[8].toString()) != 0.0) {
					balanceStr = "Gift : " + f.format(transactionObjArr[8])
							+ " Loyalty : " + f.format(transactionObjArr[7]);
				} else if ((transactionObjArr[7] == null || Double
						.parseDouble(transactionObjArr[7].toString()) == 0.0)
						&& transactionObjArr[8] != null
						&& !transactionObjArr[8].toString().isEmpty()) {
					balanceStr = f.format(transactionObjArr[8]);
				} else if (transactionObjArr[7] != null
						&& !transactionObjArr[7].toString().isEmpty()
						&& (transactionObjArr[8] == null || Double
								.parseDouble(transactionObjArr[8].toString()) == 0.0)) {
					balanceStr = f.format(transactionObjArr[7]);
				} else {
					balanceStr = f.format(0.00) + "";
				}
	
				if (OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
						.equalsIgnoreCase(transactionObjArr[3].toString())) {
					String transferBalStr = "";
					if (transactionObjArr[17] != null
							&& !transactionObjArr[17].toString().isEmpty()
							&& Double.parseDouble(transactionObjArr[17]
									.toString()) != 0.0
							&& transactionObjArr[18] != null
							&& !transactionObjArr[18].toString().isEmpty()
							&& Double.parseDouble(transactionObjArr[18]
									.toString()) != 0.0) {
						transferBalStr = "Gift : "
								+ f.format(Double.valueOf(transactionObjArr[18]
										.toString()))
								+ " Loyalty : "
								+ f.format(Double.valueOf(transactionObjArr[17]
										.toString()));
					} else if ((transactionObjArr[17] == null || Double
							.parseDouble(transactionObjArr[17].toString()) == 0.0)
							&& transactionObjArr[18] != null
							&& !transactionObjArr[18].toString().isEmpty()) {
						transferBalStr = f.format(Double
								.valueOf(transactionObjArr[18].toString()));
					} else if (transactionObjArr[17] != null
							&& !transactionObjArr[17].toString().isEmpty()
							&& (transactionObjArr[18] == null || Double
									.parseDouble(transactionObjArr[18]
											.toString()) == 0.0)) {
						transferBalStr = f.format(Double
								.valueOf(transactionObjArr[17].toString()));
					}
	
					balanceStr += transferBalStr.trim().isEmpty() ? ""
							: " [Bal. Transferred-" + transferBalStr + "]";
				}
	
				/*
				 * lc = new Listcell(); lc.setStyle("padding-left:10px;");
				 * lc.setLabel(transactionObj.getAmountBalance() == null ?
				 * f.format(0.00) :
				 * f.format(transactionObj.getAmountBalance()));
				 * lc.setTooltiptext(transactionObj.getAmountBalance() == null ?
				 * f.format(0.00) :
				 * f.format(transactionObj.getAmountBalance()));
				 * lc.setParent(li);
				 */
	
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(balanceStr);
				lc.setTooltiptext(balanceStr);
				lc.setParent(li);
	
				balanceStr = transactionObjArr[9] == null ? 0 + ""
						: transactionObjArr[9].toString();
	
				balanceStr += OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
						.equalsIgnoreCase(transactionObjArr[3].toString())
						&& transactionObjArr[19] != null ? " [Bal. Transferred-"
						+ Double.valueOf(transactionObjArr[19].toString())
								.intValue() + "]"
						: "";
	
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(balanceStr);
				lc.setTooltiptext(balanceStr);
				lc.setParent(li);
				 
				
				
				/*
				 * SELECT cl.card_number, tc.trans_child_id, tc.store_number,
				 * tc.transaction_type, tc.entered_amount_type,
				 * tc.entered_amount, " + " tc.excluded_amount,
				 * tc.amount_balance, tc.gift_balance, tc.points_balance,
				 * tc.created_date, cl.holdpoints_balance,
				 * cl.holdAmount_balance,tc.earn_type, tc.description2" + " FROM
				 * loyalty_transaction_child tc, contacts_loyalty cl
				 */
				String holdBalanceStr = "";
				if ((OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE
						.equalsIgnoreCase(transactionObjArr[3].toString()) && transactionObjArr[4]
						.toString().equalsIgnoreCase(
								OCConstants.LOYALTY_TYPE_PURCHASE))
						|| (transactionObjArr[3].toString()
								.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT))) {
					if ((transactionObjArr[11] != null && (((Double) transactionObjArr[11])
							.intValue() > 0 || (String) transactionObjArr[14] != null))
							&& transactionObjArr[13] != null
							&& ((String) transactionObjArr[13])
									.equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
						double holdBal = 0;
						holdBal = (Double) transactionObjArr[11];
						if ((String) transactionObjArr[14] != null) {
							if(transactionObjArr[14].toString().contains(Constants.ADDR_COL_DELIMETER)) {
								String transactions[] = ((String) transactionObjArr[14])
										.split(Constants.ADDR_COL_DELIMETER);
								for (String eachTrx : transactions) {
									if(eachTrx.contains(":"))
									holdBal += Double.valueOf(eachTrx.split(":")[1]
											.trim());
								}
							}
						}
						holdBalanceStr = holdBal != 0 ? new Double(holdBal)
								.intValue() + " Points" : "";
					} else if (transactionObjArr[12] != null) {
						double holdBal = 0;
						holdBal = (Double) transactionObjArr[12];
						if ((String) transactionObjArr[14] != null) {
							if(transactionObjArr[14].toString().contains(Constants.ADDR_COL_DELIMETER)) {
								String transactions[] = ((String) transactionObjArr[14]).split(Constants.ADDR_COL_DELIMETER);
								for (String eachTrx : transactions) {
									if(eachTrx.contains(":"))
									holdBal += Double.valueOf(eachTrx.split(":")[1]
											.trim());
								}
							}
						}
						holdBalanceStr = holdBal != 0 ? "$ "
								+ f.format(holdBal) : "";
					}
				} else if (OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
						.equalsIgnoreCase(transactionObjArr[3].toString())) {
					if (transactionObjArr[11] != null)
						holdBalanceStr = ((Double) transactionObjArr[11])
								.intValue() + " Points";
					if (transactionObjArr[12] != null) {
						holdBalanceStr += holdBalanceStr.isEmpty() ? "" : " & ";
						holdBalanceStr += "$ "
								+ f.format(((Double) transactionObjArr[12]));
	
					}
					String sourceCardHoldBal = "";
					if (transactionObjArr[14] != null && !transactionObjArr[14].toString().isEmpty()) {
						if(transactionObjArr[14].toString().contains(OCConstants.FORM_MAPPING_SPLIT_DELIMETER)) {
							String sourceCardHold[] = transactionObjArr[14].toString().split(OCConstants.FORM_MAPPING_SPLIT_DELIMETER);
							sourceCardHoldBal += sourceCardHold[0].split(":")[1].trim().isEmpty() ? "": (Double.valueOf(sourceCardHold[0].split(":")[1].trim())).intValue()	+ " Points ";
							
							if(sourceCardHold[1] != null && !sourceCardHold[1].isEmpty()){
							String[] sourceHolderArr = sourceCardHold[1].split(":");
							logger.info("SourceCard Hold Amount::::" +sourceHolderArr);
							if (sourceCardHold.length>0 && sourceHolderArr.length>1 && !sourceHolderArr[1].trim().isEmpty()) {
							sourceCardHoldBal += sourceCardHoldBal.trim()
										.length() > 0 ? " & " : "";
								sourceCardHoldBal += "$ "
										+ f.format((Double
												.valueOf(sourceCardHold[1]
														.split(":")[1].trim())));
							}
							}
						}
					}
					// holdBalanceStr = holdBalanceStr.trim().isEmpty() ?
					// sourceCardHoldBal.trim() : holdBalanceStr;
					holdBalanceStr += sourceCardHoldBal.trim().length() > 0 ? " [Bal. Transferred-"
							+ sourceCardHoldBal + "]"
							: "";
				} else {
					holdBalanceStr = "";
				}
				
				if(!holdBalanceStr.isEmpty() && holdBalanceStr.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(currentUser.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					holdBalanceStr = holdBalanceStr.contains("$")? holdBalanceStr.replace("$", userCurrencySymbol):holdBalanceStr; 
				}
	
				/*
				 * if( transactionObjArr[11] != null &&
				 * !transactionObjArr[11].toString().isEmpty()){ holdBalanceStr
				 * = transactionObjArr[11]+" Points"; }else
				 * if((transactionObjArr[11] == null ||
				 * transactionObjArr[11].toString().isEmpty() ||
				 * Long.parseLong(transactionObjArr[11].toString()) == 0) &&
				 * transactionObjArr[12] != null &&
				 * Double.parseDouble(transactionObjArr[12].toString()) != 0.0
				 * && !transactionObjArr[12].toString().isEmpty()){
				 * holdBalanceStr = "$ "+f.format(transactionObjArr[12]); }else
				 * { holdBalanceStr = ""; }
				 */
	
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(holdBalanceStr);
				lc.setTooltiptext(holdBalanceStr);
				lc.setParent(li);
				
			  /* lc = new Listcell();
			   String lifeTimePoints  = transactionObjArr[20] == null ? 0 + ""
						: transactionObjArr[20].toString();
			  
				lc.setLabel(lifeTimePoints+"");
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(lifeTimePoints+"");
				lc.setParent(li);
	            li.setHeight("30px");
				li.setParent(transListLbId);*/
				
				 lc = new Listcell();
				   String tierId  = transactionObjArr[21] == null ? 0 + ""
							: transactionObjArr[21].toString();
				    
				   LoyaltyProgramTierDao loyaltyProgramTierDao;
				   Long tier = Long.valueOf(tierId);
				try {
					loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					 LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(tier);
					    if(loyaltyProgramTier != null){
						lc.setLabel(loyaltyProgramTier.getTierType()+"");
						lc.setStyle("padding-left:10px;");
						lc.setTooltiptext(loyaltyProgramTier.getTierType()+"");
						lc.setParent(li);
			            li.setHeight("30px");
						li.setParent(transListLbId);
					    }else{
					    	lc.setLabel("");
					    	lc.setStyle("padding-left:10px;");
							lc.setTooltiptext("");
							lc.setParent(li);
				            li.setHeight("30px");
							li.setParent(transListLbId);
					    	 }
				} catch (Exception e) {
					logger.error("Exception ::",e);
				
				}
				  
				}
		}
	
	}

	/*public void onSelect$subsidiaryLbId(){// APP-4728 - commented
		
		String subsidiaryNo = null;
		
		if (!subsidiaryLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			subsidiaryNo = subsidiaryLbId.getSelectedItem().getValue().toString();
		}
		
		logger.info("subsidiaryNumber in onselect$subsidiaryLbId::"+subsidiaryNo);
		
		/*for (Listitem item : subsidiaryLbId.getSelectedItems()) {
			if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if (subsidiaryNo.length() == 0) {
					subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
				} else {
					subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
				}
			}
		}*/
		
		/*if(subsidiaryNo != null){
			List<Object[]> ltyStoreList = ltyPrgmSevice.findAllStoresForSubsdiaries(userId,subsidiaryNo);
			logger.info("loyaltystoreIdList" + ltyStoreList);
			Long orgId = ltyPrgmSevice.getOrgId(userId);
			logger.info("orgId" + orgId);
			List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
			logger.info("storeIdList" + storeIdList);
			if (storeIdList == null || storeIdList.size() == 0)return;
			Components.removeAllChildren(storeLbId);

			Listitem storeItem = null;
			outer: for (Object[] obj : ltyStoreList) {
				for (OrganizationStores org : storeIdList) {
					if (org.getHomeStoreId().equalsIgnoreCase(obj[1].toString()) &&
							org.getSubsidiaryId().equalsIgnoreCase(obj[0].toString())) {
						storeItem = new Listitem(org.getStoreName(), obj[1]);
						storeItem.setParent(storeLbId);
						continue outer;
					}
				}
				storeItem = new Listitem("Store ID " + obj[1],  obj[1]);

				storeItem.setParent(storeLbId);
			}
		}else{
			setDefaultStores();
		}
		
	}*/

	public void onSelect$pageSizeLbId() {

		try {
			String transType = null;
			if (!transactionsLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				transType = transactionsLbId.getSelectedItem().getValue()
						.toString();
			}
			String storeNo = Constants.STRING_NILL;
			String subsidiaryNo = Constants.STRING_NILL;
			for (Listitem item : subsidiaryLbId.getSelectedItems()) {
				if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if (subsidiaryNo.length() == 0) {
						subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
					} else {
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
					}
				}
			}
			for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + item.getValue() + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			/*for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
					String storeSub = (String) item.getValue();
					String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + store[0] + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ store[0] + "'";
					}
					if(subsidiaryNo.length() == 0)
						subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
					else
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
								+ store[1] + "'";
				}

			}*/
			logger.info("Selected StoreNumber:::"+storeNo);
			Long cardsetId = null;
			if (!cardsetLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem()
						.getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem()
						.getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			for (Listitem item : employeeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'" + item.getValue()
								+ "'";
					} else {
						employeeIdStr = employeeIdStr
								+ Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}

			String key = cardSearchBoxId.getValue();
			int totalSize = 0;
			if (key.trim().length() == 0) {
				totalSize = ltyPrgmSevice.getAllTransactionsCount(userId,
						prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), key.trim(),
						transType, storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
			} else {
				totalSize = ltyPrgmSevice
						.getAllTransactionsCount(userId, prgmId,
								MyCalendar.calendarToString(startDate, null),
								MyCalendar.calendarToString(endDate, null),
								key.trim(), transType, storeNo, subsidiaryNo, cardsetId,
								destCards, employeeIdStr,tierId);
			}
			loyaltyListBottomPagingId.setTotalSize(totalSize);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
					.getLabel());
			loyaltyListBottomPagingId.setPageSize(pageSize);
			loyaltyListBottomPagingId.setActivePage(0);

			List<Object[]> transList = null;
				transList = ltyPrgmSevice
						.getAllTransactions(
								userId,
								prgmId,
								MyCalendar.calendarToString(startDate, null),
								MyCalendar.calendarToString(endDate, null),
								loyaltyListBottomPagingId.getActivePage()
										* loyaltyListBottomPagingId
												.getPageSize(),
								loyaltyListBottomPagingId.getPageSize(),
								key, transType, storeNo, subsidiaryNo, cardsetId,
								destCards, employeeIdStr,tierId);
			redrawTransList(transList);

		} catch (Exception e) {
			logger.error("Exception");
		}

	}

	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if (event.getTarget() instanceof Paging) {

			Paging paging = (Paging) event.getTarget();

			int desiredPage = paging.getActivePage();
			this.loyaltyListBottomPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;

			String transType = null;
			if (!transactionsLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				transType = transactionsLbId.getSelectedItem().getValue()
						.toString();
			}
			String storeNo = Constants.STRING_NILL;
			String subsidiaryNo = Constants.STRING_NILL;
			for (Listitem item : subsidiaryLbId.getSelectedItems()) {
				if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if (subsidiaryNo.length() == 0) {
						subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
					} else {
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
					}
				}
			}
			for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + item.getValue() + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			/*for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
					String storeSub = (String) item.getValue();
					String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + store[0] + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ store[0] + "'";
					}
					if(subsidiaryNo.length() == 0)
						subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
					else
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
								+ store[1] + "'";
				}

			}*/
			Long cardsetId = null;
			if (!cardsetLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem()
						.getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem()
						.getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			for (Listitem item : employeeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'" + item.getValue()
								+ "'";
					} else {
						employeeIdStr = employeeIdStr
								+ Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}

			String key = cardSearchBoxId.getValue();
			List<Object[]> transList = null;
				transList = ltyPrgmSevice.getAllTransactions(userId, prgmId,
						MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), ofs, pSize,
						key, transType, storeNo, subsidiaryNo, cardsetId, destCards,
						employeeIdStr,tierId);
			redrawTransList(transList);

		}// if
	}

	public void onChanging$cardSearchBoxId(InputEvent event) {

		String transType = null;
		if (!transactionsLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			transType = transactionsLbId.getSelectedItem().getValue()
					.toString();
		}
		String storeNo = Constants.STRING_NILL;
		String subsidiaryNo = Constants.STRING_NILL;
		for (Listitem item : subsidiaryLbId.getSelectedItems()) {
			if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if (subsidiaryNo.length() == 0) {
					subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
				} else {
					subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
				}
			}
		}
		for (Listitem item : storeLbId.getSelectedItems()) {
			if (item.getValue() != null
					&& !item.getValue().toString().equalsIgnoreCase("All")) {
				if (storeNo.length() == 0) {
					storeNo = storeNo + "'" + item.getValue() + "'";
				} else {
					storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
							+ item.getValue() + "'";
				}
			}
		}
		/*for (Listitem item : storeLbId.getSelectedItems()) {
			if (item.getValue() != null
					&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
				String storeSub = (String) item.getValue();
				String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
				if (storeNo.length() == 0) {
					storeNo = storeNo + "'" + store[0] + "'";
				} else {
					storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
							+ store[0] + "'";
				}
				if(subsidiaryNo.length() == 0)
					subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
				else
					subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
							+ store[1] + "'";
			}

		}*/
		Long cardsetId = null;
		if (!cardsetLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue()
					.toString());
		}
		Long tierId = null;
		if (!tierLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			tierId = Long.parseLong(tierLbId.getSelectedItem().getValue()
					.toString());
		}
		String employeeIdStr = Constants.STRING_NILL;
		for (Listitem item : employeeLbId.getSelectedItems()) {
			if (item.getValue() != null
					&& !item.getValue().toString().equalsIgnoreCase("All")) {
				if (employeeIdStr.length() == 0) {
					employeeIdStr = employeeIdStr + "'" + item.getValue() + "'";
				} else {
					employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA
							+ "'" + item.getValue() + "'";
				}
			}
		}

		String key = event.getValue();
			int totalSize = ltyPrgmSevice.getAllTransactionsCount(userId,
					prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null), key,
					transType, storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
			loyaltyListBottomPagingId.setTotalSize(totalSize);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
					.getLabel());
			loyaltyListBottomPagingId.setPageSize(pageSize);
			loyaltyListBottomPagingId.setActivePage(0);

			List<Object[]> transList = ltyPrgmSevice.getAllTransactions(userId,
					prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),
					loyaltyListBottomPagingId.getActivePage()
							* loyaltyListBottomPagingId.getPageSize(),
					loyaltyListBottomPagingId.getPageSize(), key, transType,
					storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
			redrawTransList(transList);


	}// onChanging$emailSearchBoxId

	public void onClick$cardResetBtnId() {

		try {
			String transType = null;
			if (!transactionsLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				transType = transactionsLbId.getSelectedItem().getValue()
						.toString();
			}
			String storeNo = Constants.STRING_NILL;
			String subsidiaryNo = Constants.STRING_NILL;
			for (Listitem item : subsidiaryLbId.getSelectedItems()) {
				if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if (subsidiaryNo.length() == 0) {
						subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
					} else {
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
					}
				}
			}
			for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + item.getValue() + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			/*for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
					String storeSub = (String) item.getValue();
					String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + store[0] + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ store[0] + "'";
					}
					if(subsidiaryNo.length() == 0)
						subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
					else
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
								+ store[1] + "'";
				}

			}*/
			Long cardsetId = null;
			if (!cardsetLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem()
						.getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem()
						.getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			for (Listitem item : employeeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'" + item.getValue()
								+ "'";
					} else {
						employeeIdStr = employeeIdStr
								+ Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			int totalSize = ltyPrgmSevice.getAllTransactionsCount(userId,
					prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null), null,
					transType, storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
			loyaltyListBottomPagingId.setTotalSize(totalSize);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
					.getLabel());
			loyaltyListBottomPagingId.setPageSize(pageSize);
			loyaltyListBottomPagingId.setActivePage(0);

			List<Object[]> transList = ltyPrgmSevice.getAllTransactions(userId,
					prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),
					loyaltyListBottomPagingId.getActivePage()
							* loyaltyListBottomPagingId.getPageSize(),
					loyaltyListBottomPagingId.getPageSize(), null, transType,
					storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
			redrawTransList(transList);
			cardSearchBoxId.setValue("");
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}// onClick$cardResetBtnId()

	public void onClick$exportBtnId() {
		createWindow();
		// anchorEvent(false);

		custExport.setVisible(true);
		custExport.doHighlighted();
	} // onClick$exportLblId

	public void onClick$filterBtnId() {
				
	    int storeSelectedCount = storeLbId.getSelectedCount();
		int storeItemCount = storeLbId.getItemCount();
        if(storeSelectedCount == storeItemCount){
        	storeBandBoxId.setValue("All");
        }
        else if((storeSelectedCount>1) && !(storeSelectedCount == storeItemCount)){
			storeBandBoxId.setValue("Multiple");
		}
        else if((storeSelectedCount==1) && !(storeSelectedCount == storeItemCount)){
        	storeBandBoxId.setValue(storeLbId.getSelectedItem().getLabel());
        }
        else if(storeLbId.getSelectedIndex() == -1){
			storeBandBoxId.setValue("");
		}
		
		int empSelectedCount = employeeLbId.getSelectedCount();
		int empItemCount = employeeLbId.getItemCount();
		if(empSelectedCount == empItemCount){
			employeeBandBoxId.setValue("All");
        }
        else if((empSelectedCount>1) && !(empSelectedCount == empItemCount)){
        	employeeBandBoxId.setValue("Multiple");
		}
        else if((empSelectedCount==1) && !(empSelectedCount == empItemCount)){
        	employeeBandBoxId.setValue(employeeLbId.getSelectedItem().getLabel());
		}
        else if(employeeLbId.getSelectedIndex() == -1){
        	employeeBandBoxId.setValue("");
		}
		
		cardSearchBoxId.setValue("");
		if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Custom Dates")) {
			if (!isValidate()) {
				return;
			}
		}

		getDateValues();

		plotLblId.setValue(MyCalendar.calendarToString(startDate,
				MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)
				+ " - "
				+ MyCalendar.calendarToString(endDate,
						MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));
		listLblId.setValue(MyCalendar.calendarToString(startDate,
				MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)
				+ " - "
				+ MyCalendar.calendarToString(endDate,
						MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));

		String transType = null;
		if (!transactionsLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			transType = transactionsLbId.getSelectedItem().getValue().toString();
		}
		String storeNo = Constants.STRING_NILL;
		String subsidiaryNo = Constants.STRING_NILL;
		
		for (Listitem item : subsidiaryLbId.getSelectedItems()) {
			if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if (subsidiaryNo.length() == 0) {
					subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
				} else {
					subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
				}
			}
		}
		
		for (Listitem item : storeLbId.getSelectedItems()) {
			if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if (storeNo.length() == 0) {
					storeNo = storeNo + "'" + item.getValue() + "'";
				} else {
					storeNo = storeNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
				}
			}
		}
		/*for (Listitem item : storeLbId.getSelectedItems()) {
			if (item.getValue() != null
					&& !storeBandBoxId.getValue().toString().equalsIgnoreCase("All")) {
				String storeSub = (String) item.getValue();
				String[] store = storeSub.split(Constants.DELIMETER_DOUBLECOLON);
				if (storeNo.length() == 0) {
					storeNo = storeNo + "'" + store[0] + "'";
				} else {
					storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
							+ store[0] + "'";
				}
				if(subsidiaryNo.length() == 0)
					subsidiaryNo = subsidiaryNo + "'" + store[1] + "'";
				else
					subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'"
							+ store[1] + "'";
			}

		}*/
		
		Long cardsetId = null;
		if (!cardsetLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
		}
		
		Long  tierId = null;
		if (!tierLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
		}
		String employeeIdStr = Constants.STRING_NILL;
		for (Listitem item : employeeLbId.getSelectedItems()) {
			if (item.getValue() != null
					&& !item.getValue().toString().equalsIgnoreCase("All")) {
				if (employeeIdStr.length() == 0) {
					employeeIdStr = employeeIdStr + "'" + item.getValue() + "'";
				} else {
					employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
				}
			}
		}
		plot1.setEngine(new LineChartEngine());
		this.setPlotData();

		/*redrawTransactionsCount(prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), storeNo, cardsetId,
				employeeIdStr,tierId);*/
		redrawTransactionsCount(prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), subsidiaryNo, storeNo, cardsetId,
				employeeIdStr,tierId);

		int totalSize = ltyPrgmSevice.getAllTransactionsCount(userId, prgmId,
				MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), null, transType,
				storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);

		loyaltyListBottomPagingId.setTotalSize(totalSize);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem()
				.getLabel());
		loyaltyListBottomPagingId.setPageSize(pageSize);
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.addEventListener("onPaging", this);

		List<Object[]> transList = ltyPrgmSevice.getAllTransactions(userId,
				prgmId, MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null),
				loyaltyListBottomPagingId.getActivePage()
						* loyaltyListBottomPagingId.getPageSize(),
				loyaltyListBottomPagingId.getPageSize(), null, transType,
				storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);

		redrawTransList(transList);
	}

	private boolean isValidate() {

		if (fromDateboxId.getValue() != null
				&& !fromDateboxId.getValue().toString().isEmpty()) {
			startDate = MyCalendar.getNewCalendar();
			startDate.setTime(fromDateboxId.getValue());
			logger.debug("startDate ::"
					+ MyCalendar.calendarToString(startDate,
							MyCalendar.FORMAT_DATETIME_STYEAR));
		} else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red",
					"TOP");
			return false;
		}

		if (startDate == null) {
			return false;
		}

		if (toDateboxId.getValue() != null
				&& !toDateboxId.getValue().toString().isEmpty()) {
			endDate = MyCalendar.getNewCalendar();
			endDate.setTime(toDateboxId.getValue());
			logger.debug("endDate ::"+ MyCalendar.calendarToString(endDate,MyCalendar.FORMAT_DATETIME_STYEAR));
		} else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red","TOP");
			return false;
		}

		if (endDate == null) {
			return false;
		}
		if (endDate.before(startDate)) {
			MessageUtil.setMessage("To date must be later than From date","color:red", "TOP");
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (!sdf.format(startDate.getTime()).equals(
				sdf.format(prgmObj.getCreatedDate().getTime()))) {
			if (startDate.before(prgmObj.getCreatedDate())) {
				MessageUtil.setMessage("From date should be after the program creation date.","color:red", "TOP");
				return false;
			}
		}
		return true;
	}

	public void onSelect$timeDurLbId() {
		if (timeDurLbId.getSelectedItem().getLabel()
				.equalsIgnoreCase("Custom Dates")) {
			fromDateboxId.setText("");
			toDateboxId.setText("");
			datesDivId.setVisible(true);
		} else {
			datesDivId.setVisible(false);
		}
	}

	public void onClick$selectAllAnchr$custExport() {

		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {

		anchorEvent(false);
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

		if (checked) {

			int confirm = Messagebox.show(
					"Do you want to export with selected field(s) ?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {

					exportCSV((String) exportCbId.getSelectedItem().getValue(),
							indexes);

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
				}
			} else {
				custExport.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

	}
	private void exportCSV(String value, int[] indexes) {
		logger.debug("-- just entered --");
		String type = exportCbId.getSelectedItem().getLabel();
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String) PropertyUtil
				.getPropertyValue("usersParentDirectory");
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

		if (type.contains("csv")) {

			DecimalFormat f = new DecimalFormat("#0.00");
			String prgmName = prgmObj.getProgramName();
			if (prgmName.contains("/")) {

				prgmName = prgmName.replace("/", "_");

			}
			String transType = null;
			if (!transactionsLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				transType = transactionsLbId.getSelectedItem().getValue()
						.toString();
			}
			String storeNo = Constants.STRING_NILL;
			String subsidiaryNo = Constants.STRING_NILL;
			for (Listitem item : subsidiaryLbId.getSelectedItems()) {
				if (item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if (subsidiaryNo.length() == 0) {
						subsidiaryNo = subsidiaryNo + "'" + item.getValue() + "'";
					} else {
						subsidiaryNo = subsidiaryNo + Constants.DELIMETER_COMMA + "'" + item.getValue() + "'";
					}
				}
			}
			for (Listitem item : storeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (storeNo.length() == 0) {
						storeNo = storeNo + "'" + item.getValue() + "'";
					} else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}
			Long cardsetId = null;
			if (!cardsetLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem()
						.getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem()
						.getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			for (Listitem item : employeeLbId.getSelectedItems()) {
				if (item.getValue() != null
						&& !item.getValue().toString().equalsIgnoreCase("All")) {
					if (employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'" + item.getValue()
								+ "'";
					} else {
						employeeIdStr = employeeIdStr
								+ Constants.DELIMETER_COMMA + "'"
								+ item.getValue() + "'";
					}
				}
			}

			String key = cardSearchBoxId.getValue();

			String filePath = exportDir
					+ "Loyalty_Report_"
					+ prgmName
					+ "_"
					+ MyCalendar.calendarToString(Calendar.getInstance(),
							MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			try {
				filePath = filePath + "_Transactions.csv";
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				int count = 0;
					count = ltyPrgmSevice.getAllTransactionsCount(userId,
							prgmId,
							MyCalendar.calendarToString(startDate, null),
							MyCalendar.calendarToString(endDate, null),
							key, transType, storeNo, subsidiaryNo, cardsetId,
							employeeIdStr,tierId);
				
				if (count == 0) {
					Messagebox
							.show("No transactions found with the given search criteria.",
									"Info", Messagebox.OK,
									Messagebox.INFORMATION);
					return;
				}
				String udfFldsLabel = "";
				String dummyCoulumn="";
				if (indexes[0] == 0) {

					udfFldsLabel = "\"" + "Processed Time" + "\"" + ",";
					dummyCoulumn+=",created_date";
				}

				if (indexes[1] == 0) {
					udfFldsLabel += "\"" + "Membership Number" + "\"" + ",";
					dummyCoulumn+=",udf1";
				}
				if (indexes[2] == 0) {
					udfFldsLabel += "\"" + "Source Card" + "\"" + ",";
					dummyCoulumn+=",udf2";
				}
				if (indexes[3] == 0) {
					udfFldsLabel += "\"" + "Transaction ID" + "\"" + ",";
					dummyCoulumn+=",udf3";
				}
				if (indexes[4] == 0) {
					udfFldsLabel += "\"" + "Subsidiary" + "\"" + ",";
					dummyCoulumn+=",udf4";
				}
				if (indexes[5] == 0) {
					udfFldsLabel += "\"" + "Store" + "\"" + ",";
					dummyCoulumn+=",udf5";
				}
				if (indexes[6] == 0) {

					udfFldsLabel += "\"" + "Transaction Type" + "\"" + ",";
					dummyCoulumn+=",udf6";
				}
				if (indexes[7] == 0) {

					udfFldsLabel += "\"" + "Value Entered" + "\"" + ",";
					dummyCoulumn+=",udf7";
				}
				if (indexes[8] == 0) {

					udfFldsLabel += "\"" + "Valuecode" + "\"" + ",";
					dummyCoulumn+=",udf8";
				}
				if (indexes[9] == 0) {

					udfFldsLabel += "\"" + "Amount Diff." + "\"" + ",";
					dummyCoulumn+=",udf9";
				}
				if (indexes[10] == 0) {

					udfFldsLabel += "\"" + "Points Diff." + "\"" + ",";
					dummyCoulumn+=",udf10";
				}
				if (indexes[11] == 0) {

					udfFldsLabel += "\"" + "Balance Curr." + "\"" + ",";
					dummyCoulumn+=",udf11";
				}
				if (indexes[12] == 0) {

					udfFldsLabel += "\"" + "Balance Pts." + "\"" + ",";
					dummyCoulumn+=",udf12";
				}
				
				if (indexes[13] == 0) {

					udfFldsLabel += "\"" + "Hold Balance" + "\"" + ",";
					dummyCoulumn+=",home_phone";
				}
				/*if (indexes[13] == 0) {

					udfFldsLabel += "\"" + "Lifetime Points" + "\"" + ",";
				}*/
				if (indexes[14] == 0) {

					udfFldsLabel += "\"" + "Tier" + "\"" + ",";
					dummyCoulumn+=",udf14";
				}
				if (indexes[15] == 0) {

					udfFldsLabel += "\"" + "Membership Status" + "\"" + ",";
					dummyCoulumn+=",udf15";
				}
				if (indexes[16] == 0) {

					udfFldsLabel += "\"" + "First Name" + "\"" + ",";
					dummyCoulumn+=",first_name";
				}
				if (indexes[17] == 0) {

					udfFldsLabel += "\"" + "Last Name" + "\"" + ",";
					dummyCoulumn+=",last_name";
				}
				if (indexes[18] == 0) {

					udfFldsLabel += "\"" + "Mobile Number" + "\"" + ",";
					dummyCoulumn+=",mobile_phone";
				}
				if (indexes[19] == 0) {

					udfFldsLabel += "\"" + "Email Address" + "\"" + ",";
					dummyCoulumn+=",email_id";
				}
				if (indexes[20] == 0) {

					udfFldsLabel += "\"" + "Receipt#" + "\"" + ",";
					dummyCoulumn+=",receipt_number";
				}
				
				sb = new StringBuffer();
				sb.append(udfFldsLabel);
				sb.append("\r\n");

				bw.write(sb.toString());
				LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				List<LoyaltyProgramTier> listTier = loyaltyProgramTierDao.getAllTierByProgramId(prgmId);
				Map<Long,String> tierMap=new HashMap<>();
				if(listTier!=null && listTier.size()>0)
			   for(LoyaltyProgramTier tier:listTier){
				  if(tier!=null)
				  tierMap.put(tier.getTierId(), tier.getTierType());
			   }
				logger.info(" Tier Map :"+tierMap);
				 String exportQuery="SELECT id"+dummyCoulumn+" FROM tempexportreport where 1=2";
				 logger.info(" Dummy Export query ::"+exportQuery);
 
					  String query=null;
							query = prepareQuery(userId,prgmId,
									MyCalendar.calendarToString(startDate, null),
									MyCalendar.calendarToString(endDate, null), 
									 key.trim(), transType, storeNo, subsidiaryNo,
									cardsetId, employeeIdStr,tierId);
							logger.info("Final Transaction Report Query:"+query);
						
						 sb = new StringBuffer();
						 JdbcResultsetHandler exportJdbcResultsetHandler = new JdbcResultsetHandler();
							exportJdbcResultsetHandler.executeStmt(exportQuery, true);
							ResultSet exportresResultSet = exportJdbcResultsetHandler.getResultSet();
							
						 logger.info("Final Export Query ::"+query);
							 JdbcResultsetHandler jh = null;
								jh = new JdbcResultsetHandler();
								jh.executeStmt(query, true);
								ResultSet resultSet = jh.getResultSet();
								int pk=1;
								while (resultSet.next()) {
									exportresResultSet.moveToInsertRow();
									 exportresResultSet.updateLong("id", pk++);
									if (indexes[0] == 0) {
										SimpleDateFormat formatter = new SimpleDateFormat(
												MyCalendar.FORMAT_DATETIME_STYEAR);
										Date date = null;
										try {
											date = (Date) formatter.parse(resultSet.getString("created_date").trim());
										} catch (ParseException e) {
											logger.error("Exception ::", e);
										}
										Calendar cal = Calendar.getInstance();
										cal.setTime(date);
										String createdDate = "";
										createdDate = MyCalendar.calendarToString(cal,
												MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone);

										 exportresResultSet.updateString("created_date", createdDate);
									}

									if (indexes[1] == 0) { 
										String memNuber = resultSet.getString("card_number");
										 
										if(destCardMap.get(resultSet.getString("transfered_to")) != null){
											memNuber = (String) destCardMap.get(resultSet.getString("transfered_to"));
										}else if(resultSet.getString("transfered_to") == null && OCConstants.LOYALTY_TRANS_TYPE_TRANSFER.equalsIgnoreCase(resultSet.getString("transaction_type"))){
											memNuber = ((String)resultSet.getString("description")).split("dest:")[1];
										  }
									exportresResultSet.updateString("udf1", memNuber);
									}
									if (indexes[2] == 0) {   
										 exportresResultSet.updateString("udf2", resultSet.getString("transfered_to") != null
												|| OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
														.equalsIgnoreCase(resultSet.getString("transaction_type")) ?resultSet.getString("card_number") : "");
										
									}
									if (indexes[3] == 0) {
										 exportresResultSet.updateString("udf3",  resultSet.getString("trans_child_id") );
									}
									if (indexes[4] == 0) {
										if( resultSet.getString("subsidiary_number")!= null){
											String sName = "Subsidiary ID "+ resultSet.getString("subsidiary_number") ;
										for (OrganizationStores org : storeList){
										if ( resultSet.getString("subsidiary_number").equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
									    	sName = org.getSubsidiaryName().toString();
									    	break;
										}}
										 exportresResultSet.updateString("udf4", sName );
										}else{
											 exportresResultSet.updateString("udf4",  "" );
											}
										}
									if (indexes[5] == 0) {
										if(resultSet.getString("store_number") != null){
											String sName = "Store ID "+resultSet.getString("store_number").toString() ;
										
											
										for (OrganizationStores org : storeList){
										
											if (resultSet.getString("store_number").toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()  && resultSet.getString("subsidiary_number")!=null ) {
											try {
												
												sName = resultSet.getString("subsidiary_number") == null ?( storeNameMap.containsKey(resultSet.getString("store_number").toString()) ? storeNameMap.get(resultSet.getString("store_number").toString())
													: resultSet.getString("store_number").toString() == null ? "--"
															: "Store ID " + resultSet.getString("store_number").toString()) : 
														(storeSBSNameMap.containsKey(resultSet.getString("store_number").toString()+Constants.ADDR_COL_DELIMETER+resultSet.getString("subsidiary_number") ) ? 
																storeSBSNameMap.get(resultSet.getString("store_number").toString()+Constants.ADDR_COL_DELIMETER+resultSet.getString("subsidiary_number") ) :  "Store ID " + resultSet.getString("store_number").toString() );
												}catch (Exception e) {
													logger.error("Exception ::", e);
												}
											
									    		break;
											} else if(resultSet.getString("subsidiary_number") == null){
												
												 sName = "Store ID "+resultSet.getString("store_number").toString() ;
											}
										}
										 exportresResultSet.updateString("udf5", sName );
										}else{
											 exportresResultSet.updateString("udf5", "" );
											}
										}
									if (indexes[6] == 0) {
										String transactionType = "";
										if (resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)) {
											if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(resultSet.getString("entered_amount_type"))) {
												transactionType = "Gift Issuance";
											} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD.equalsIgnoreCase(resultSet.getString("entered_amount_type"))) {
												transactionType = "Reward";
											} else {
												transactionType = "Loyalty Issuance";
											}
										} else if( resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)) {
											if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(resultSet.getString("entered_amount_type"))) {
												transactionType = "Issuance Reversal";
											} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL
													.equalsIgnoreCase(resultSet.getString("entered_amount_type"))) {
												transactionType = "Redemption Reversal";
											} else if (OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT
													.equalsIgnoreCase(resultSet.getString("entered_amount_type"))) {
												transactionType = "Store Credit";
											}
										} else {
											transactionType = resultSet.getString("transaction_type");
										}

										exportresResultSet.updateString("udf6", transactionType );
									}
									String valuecode=Constants.STRING_NILL;
									if (indexes[7] == 0) {
										String valueEntered = Constants.STRING_NILL;
										if (((resultSet.getString("earn_type") != null && resultSet.getString("earn_type").equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) || 
												resultSet.getString("entered_amount_type") != null && resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM))
												&& (!(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE) )&& 
														!(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)))) {
											Double enteredAmt =resultSet.getString("entered_amount") == null ? 0 : Double.parseDouble(resultSet.getString("entered_amount"));
											valueEntered = resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ?
													(resultSet.getString("entered_amount_type") != null ? resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "
													+ enteredAmt.longValue() : "- " + enteredAmt : "0"): "" + enteredAmt.longValue();
											valuecode="Points";
										} else {
											double enteredAmt = resultSet.getString("entered_amount") == null ? 0.0
													: Double.parseDouble(resultSet.getString("entered_amount"));
											double excludeAmt = resultSet.getString("excluded_amount") == null ? 0.0
													: Double.parseDouble(resultSet.getString("excluded_amount"));
											valueEntered = resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ?
													(resultSet.getString("entered_amount_type") != null ? (resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "
													+ f.format(enteredAmt - excludeAmt) : "- "+ f.format(enteredAmt - excludeAmt)) : "0.00"): f.format(enteredAmt - excludeAmt);
											if (resultSet.getString("entered_amount") != null) {
												valuecode="Currency";
											} 
											else if (resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)
													|| resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_BONUS)) {
												valueEntered = (resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "0" : "0.00";
												valuecode=((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "Points" : "Currency";   
											} else {
												valueEntered = "--";
												valuecode="--";
											}
										}
										exportresResultSet.updateString("udf7", valueEntered );
									}
									if (indexes[8] == 0) {
										exportresResultSet.updateString("udf8", valuecode );
									}
									String balanceStr = "";
									if (resultSet.getString("amount_balance") != null
											&& !resultSet.getString("amount_balance").isEmpty()
											&& Double.parseDouble(resultSet.getString("amount_balance")) != 0.0
											&& resultSet.getString("gift_balance") != null
											&& !resultSet.getString("gift_balance").isEmpty()
											&& Double.parseDouble(resultSet.getString("gift_balance")) != 0.0) {
										balanceStr = "Gift : " + f.format(Double.valueOf(resultSet.getDouble("gift_balance"))
												+ " Loyalty : " + f.format(Double.valueOf(resultSet.getDouble("amount_balance"))));
									} else if ((resultSet.getString("amount_balance") == null || Double
											.parseDouble(resultSet.getString("amount_balance")) == 0.0)
											&& resultSet.getString("gift_balance")!= null
											&& !resultSet.getString("gift_balance").isEmpty()) {
										balanceStr = f.format(resultSet.getDouble("gift_balance"));
									} else if (resultSet.getString("amount_balance") != null
											&& !resultSet.getString("amount_balance").isEmpty()
											&& (resultSet.getString("gift_balance") == null || Double
													.parseDouble(resultSet.getString("gift_balance")) == 0.0)) {
										balanceStr = f.format(resultSet.getDouble("amount_balance"));
									} else {
										balanceStr = f.format(0.00) + "";
									}

									if (OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
											.equalsIgnoreCase(resultSet.getString("transaction_type"))) {
										String transferBalStr = "";
										if (resultSet.getString("amount_difference") != null
												&& !resultSet.getString("amount_difference").isEmpty()
												&& Double.parseDouble(resultSet.getString("amount_difference")) != 0.0
												&& resultSet.getString("gift_difference") != null
												&& !resultSet.getString("gift_difference").isEmpty()
												&& Double.parseDouble(resultSet.getString("gift_difference")) != 0.0) {
											transferBalStr = "Gift : "
													+ f.format(Double.valueOf(resultSet.getString("gift_difference")))
													+ " Loyalty : "
													+ f.format(Double.valueOf(resultSet.getString("amount_difference")));
										} else if ((resultSet.getString("amount_difference") == null || Double
												.parseDouble(resultSet.getString("amount_difference")) == 0.0)
												&& resultSet.getString("gift_difference") != null
												&& !resultSet.getString("gift_difference").isEmpty()) {
											transferBalStr = f.format(Double
													.valueOf(resultSet.getString("gift_difference")));
										} else if (resultSet.getString("amount_difference") != null
												&& !resultSet.getString("amount_difference").isEmpty()
												&& (resultSet.getString("gift_difference") == null || Double
														.parseDouble(resultSet.getString("gift_difference")) == 0.0)) {
											transferBalStr = f.format(Double
													.valueOf(resultSet.getString("amount_difference")));
										}

										balanceStr += transferBalStr.trim().isEmpty() ? ""
												: " [Bal. Transferred-"
														+ transferBalStr + "]";
									}
									String amtdiff="";	
									if (indexes[9] == 0) {
										if (resultSet.getString("amount_difference")!= null
												&& !resultSet.getString("amount_difference").isEmpty()) {
											amtdiff = resultSet.getString("amount_difference");
										}
										else
											amtdiff="0";
										
										exportresResultSet.updateString("udf9", amtdiff );
									}
									String pointDiff="";	
									if (indexes[10] == 0) {
										if (resultSet.getString("points_difference") != null
												&& !resultSet.getString("points_difference").isEmpty()) {
											pointDiff = resultSet.getString("points_difference");
										}
										else
											pointDiff="0";
										exportresResultSet.updateString("udf10", pointDiff );
									}
									
									if (indexes[11] == 0) {
										exportresResultSet.updateString("udf11", balanceStr );
									}
									
									balanceStr = resultSet.getString("points_balance") == null ? 0 + "" : resultSet.getString("points_balance")
											.toString();

									balanceStr += OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
											.equalsIgnoreCase(resultSet.getString("transaction_type"))
											&& resultSet.getString("points_difference") != null ? " [Bal. Transferred-"
											+ Double.valueOf(resultSet.getString("points_difference"))
													.intValue() + "]"
											: "";
									if (indexes[12] == 0) {
										exportresResultSet.updateString("udf12", balanceStr );
									}
									String holdBalanceStr = "";
									if ((OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE.equalsIgnoreCase(resultSet.getString("transaction_type")) && 
											resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE))
											|| (resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT))) {
										if ((resultSet.getString("hold_points") != null && (((Double)resultSet.getDouble("hold_points"))
												.intValue() > 0  && (resultSet.getString("earn_type")
														.equalsIgnoreCase(OCConstants.LOYALTY_POINTS))))) {
											double holdBal = 0;
											holdBal = (Double) resultSet.getDouble("hold_points");
											if ((String) resultSet.getString("description2") != null) {
												String transactions[] = ((String)resultSet.getString("description2"))
														.split(Constants.ADDR_COL_DELIMETER);
												for (String eachTrx : transactions) {
													if(eachTrx.contains(":"))
													holdBal += Double.valueOf(eachTrx
															.split(":")[1].trim());
												}
											}
											holdBalanceStr = holdBal != 0 ? new Double(
													holdBal).intValue() + " Points"
													: "";
										} else if (resultSet.getString("hold_amount") != null) {
											double holdBal = 0;
											holdBal = (Double)resultSet.getDouble("hold_amount");
											if  (resultSet.getString("description2") != null) {
												String transactions[] =  resultSet.getString("description2")
														.split(Constants.ADDR_COL_DELIMETER);
												for (String eachTrx : transactions) {
													if(eachTrx.contains(":"))
													holdBal += Double.valueOf(eachTrx
															.split(":")[1].trim());
												}
											}
											holdBalanceStr = holdBal != 0 ? "$ "
													+ f.format(holdBal) : "";
										}
									} else if (OCConstants.LOYALTY_TRANS_TYPE_TRANSFER
											.equalsIgnoreCase(resultSet.getString("transaction_type"))) {
										if (resultSet.getString("hold_points") != null)
											holdBalanceStr = ((Double) resultSet.getDouble("hold_points"))
													.intValue() + " Points";
										if (resultSet.getString("hold_amount") != null) {
											holdBalanceStr += holdBalanceStr.isEmpty() ? ""
													: " & ";
											holdBalanceStr += "$ "
													+ f.format(((Double)resultSet.getDouble("hold_amount")));

										}
										String sourceCardHoldBal = "";
										if (resultSet.getString("description2") != null) {
											String sourceCardHold[] = resultSet.getString("description2")
													.split(OCConstants.FORM_MAPPING_SPLIT_DELIMETER);
											sourceCardHoldBal += sourceCardHold[0]
													.split(":")[1].trim().isEmpty() ? ""
													: (Double.valueOf(sourceCardHold[0]
															.split(":")[1].trim()))
															.intValue()
															+ " Points ";
											if (!sourceCardHold[1].split(":")[1].trim()
													.isEmpty()) {
												sourceCardHoldBal += sourceCardHoldBal
														.trim().length() > 0 ? " & "
														: "";
												sourceCardHoldBal += "$ "
														+ f.format((Double
																.valueOf(sourceCardHold[1]
																		.split(":")[1]
																		.trim())));
											}
										}
										holdBalanceStr += sourceCardHoldBal.trim()
												.length() > 0 ? " [Bal. Transferred-"
												+ sourceCardHoldBal + "]" : "";
									} else {
										holdBalanceStr = "";
									}
									if (indexes[13] == 0) {
										exportresResultSet.updateString("home_phone", holdBalanceStr );
									  }
									   if(indexes[14] == 0) {
										    	exportresResultSet.updateString("udf14", tierMap.get(resultSet.getLong("tier_id")));
									   }
									  if(indexes[15]==0){
										  exportresResultSet.updateString("udf15",resultSet.getString("membership_status"));
									   }
											if(indexes[16]==0){
												exportresResultSet.updateString("first_name",resultSet.getString("first_name"));
											}
											if(indexes[17]==0){
												exportresResultSet.updateString("last_name",resultSet.getString("last_name"));
											}
											if(indexes[18]==0){
												exportresResultSet.updateString("mobile_phone",resultSet.getString("mobile_phone"));
											}
											if(indexes[19]==0){
												exportresResultSet.updateString("email_id",resultSet.getString("email_id"));
											}
											if(indexes[20]==0){
												exportresResultSet.updateString("receipt_number",resultSet.getString("receipt_number"));
											}
		                              
								exportresResultSet.insertRow();
								exportresResultSet.moveToCurrentRow();
					        }
							OCCSVWriter csvWriter = null;
							try{
							csvWriter = new OCCSVWriter(bw);
							csvWriter.writeAll(exportresResultSet, false, 1);
							}
							catch(Exception e){
							logger.error("Exception during writing csv file ",e);	
						     }  
							
							finally {
								if(csvWriter!=null){
									csvWriter.flush();
									csvWriter.close();
										}
							if(jh!=null)
							jh.destroy();
							if(exportJdbcResultsetHandler!=null)
							exportJdbcResultsetHandler.destroy();
							jh=null; bw=null; sb=null;
							exportJdbcResultsetHandler=null;
							csvWriter=null;
							exportresResultSet=null;
							resultSet=null;
						}
							Filedownload.save(file, "text/csv");
						
						}
						catch (Exception e) {
							logger.info("Exception ", e);
						}
						logger.debug("exited");
		       }
					
			}	
		
		private String prepareQuery(Long userId,
			Long prgmId, String startDateStr, String endDateStr,String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId,String employeeIdStr,Long tierId ) {
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key+"%'";
		}
		if(transType != null) {
			if(transType.equalsIgnoreCase("loyaltyIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE + "' ";
			}
			else if(transType.equalsIgnoreCase("giftIssuance")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT + "' ";
			}
			else if(transType.equalsIgnoreCase("Returns")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND (tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL + "' OR"
						+ " tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL + "')";
			}
			else if(transType.equalsIgnoreCase("StoreCredit")) {
				subQry += " AND tc.transaction_type ='" + OCConstants.LOYALTY_TRANS_TYPE_RETURN + "' AND tc.entered_amount_type = '" + OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT + "' ";
			}
			else {
				subQry += " AND tc.transaction_type ='" + transType + "' ";
			}
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND tc.store_number in ("+storeNo+")";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND tc.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND tc.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND tc.tier_id =" + tierId.longValue();
		}
		if(employeeIdStr != null && employeeIdStr.length() != 0 ){
			subQry += " AND tc.employee_id in ("+employeeIdStr+")";
		}
		
		String qry=" SELECT cl.card_number, tc.trans_child_id, tc.store_number, tc.transaction_type, tc.entered_amount_type, tc.entered_amount, " +
				" tc.excluded_amount, tc.amount_balance, tc.gift_balance, tc.points_balance, tc.created_date,  tc.hold_points, tc.hold_amount, tc.earn_type, "
				+ "tc.description2, tc.transfered_to, tc.description, tc.amount_difference, tc.receipt_number,gift_difference, points_difference,cl.total_loyalty_earned,tc.tier_id,tc.subsidiary_number, " +
				"cl.membership_status,cl.contact_id ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id  FROM loyalty_transaction_child tc, contacts_loyalty cl,contacts cc " +
				" WHERE cl.loyalty_id = tc.loyalty_id  and  cl.contact_id=cc.cid" +
				" AND tc.user_id = " + userId.longValue() + " AND tc.program_id = " + prgmId.longValue() + subQry +
				" AND tc.created_date BETWEEN '"+startDateStr+"' AND '"+endDateStr+"'  "+
				" ORDER BY tc.trans_child_id DESC";
		return qry;
	
	}

	public void createWindow() {

		try {

			Components.removeAllChildren(custExport$chkDivId);
			
			Checkbox tempChk2 = new Checkbox("Processed Time");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Membership Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Source Card");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Transaction ID");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Subsidiary Name.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Store Name.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Transaction Type");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Value Entered");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Valuecode");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Amount Diff.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			tempChk2 = new Checkbox("Points Diff.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			tempChk2 = new Checkbox("Balance Curr.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Balance Pts.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Hold Balance");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			/*tempChk2 = new Checkbox("Lifetime Points");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);*/
			
			tempChk2 = new Checkbox("Tier");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Membership Status");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("First Name");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Last Name");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Mobile Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Email Address"); 
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
	
		//receiptno	
			tempChk2 = new Checkbox("Receipt#"); 
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

}
