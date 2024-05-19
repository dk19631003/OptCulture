package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
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
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
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

public class LtyRegistrationReportController  extends GenericForwardComposer{
	
	TimeZone clientTimeZone ;
	private Chart plot1;
	private LoyaltyProgramService ltyPrgmSevice;
	private Long userId;
	private Users currentUser;
	private  String  userCurrencySymbol = "$ ";
	private Long prgmId;
	private LoyaltyProgram prgmObj;
	private Paging loyaltyListBottomPagingId;
	private Listbox regReportLbId,pageSizeLbId,timeDurLbId,cardsetLbId,storeLbId,statusLbId,employeeLbId,tierLbId, subsidiaryLbId;
	private Checkbox transCardsChkId;
	private Textbox cardSearchBoxId;
	private Combobox exportCbId,exporttierCbId;
	private Rows tierbreakRowsId;
	private Bandbox storeBandBoxId,employeeBandBoxId;
	private Foot tierbreakFooterId;
	private Columns tierbreakColsId;
	private Div datesDivId,custExport$chkDivId,tierbreakDivId,exportDivId,cardSetDivId;
	private Label plotLblId,tierbreakLblId;
	private MyDatebox fromDateboxId,toDateboxId;
	private static final String DATE_DIFF_TYPE_DAYS = "days";
	private static final String DATE_DIFF_TYPE_MONTHS = "months";
	private static final String DATE_DIFF_TYPE_YEARS = "years";
	private String typeDiff;
	private Calendar startDate,endDate;
	private int monthsDiff;
	private List<OrganizationStores> storeList;
	private Window custExport;
	
	public static Map<String,String> MONTH_MAP = new HashMap<String, String>();
	
	static{
		
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
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LtyRegistrationReportController() {
		ltyPrgmSevice = new LoyaltyProgramService();
		userId = GetUser.getUserObj().getUserId();
		currentUser = GetUser.getUserObj();
		session = Sessions.getCurrent();
		prgmId = (Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
	}

	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		storeBandBoxId.setTooltiptext("Store's Subsidiary is enclosed in braces");
		logger.debug(":: before start time in doAfterCompose compose time in millis ::"+System.currentTimeMillis());
		if(prgmObj.getTierEnableFlag() == 'Y'){
			tierbreakDivId.setVisible(true);
			exportDivId.setVisible(true);
		}else{
			tierbreakDivId.setVisible(false);
			exportDivId.setVisible(false);
		}
		exportCbId.setSelectedIndex(0);
		exporttierCbId.setSelectedIndex(0);
		storeList = ltyPrgmSevice.getAllStores(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		getDateValues();
		setDefaultSubsidiaries();
		setDefaultStores();
		//setEmployeeIds();//APP-4728
		if(prgmObj.getTierEnableFlag() == 'Y'){
			tierLbId.setDisabled(false);
			setTiers();
		}else{
			tierLbId.setDisabled(true);	
		}
		resetBreakTierCols();
		
		
		if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			cardsetLbId.setDisabled(false);
			cardSetDivId.setVisible(true);
			setCardSets();
		}else {
			cardsetLbId.setDisabled(true);
			cardSetDivId.setVisible(false);
		}
		
		tierbreakLblId.setValue(MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - " +
				MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) );

		plotLblId.setValue(MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - " +
								MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) );
		
		plot1.setEngine(new LineChartEngine());
		this.setPlotData();
		
		int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId,MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), null, null,null,null,false,statusLbId.getSelectedItem().getValue().toString(),null,null);

		loyaltyListBottomPagingId.setTotalSize(totalSize);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		loyaltyListBottomPagingId.setPageSize(pageSize);
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.addEventListener("onPaging", this); 
		List<Object[]> contactLtyList = null;
		/*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
				loyaltyListBottomPagingId.getPageSize(),null,null,null,false,statusLbId.getSelectedItem().getValue().toString(),null,null);*/
		contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
				loyaltyListBottomPagingId.getPageSize(),null,null,null,null,false,statusLbId.getSelectedItem().getValue().toString(),null,null);
		/*redrawTierSummary(prgmId,MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null),false,null,null,null,null);*/
		redrawTierSummary(prgmId,MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null),false,null,null,null,null,null);
		redraw(contactLtyList);
		
	}

	/*public void onSelect$subsidiaryLbId(){// APP-4728 - commented

		String subsidiaryNo = null;

		if (!subsidiaryLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase("All")) {
			subsidiaryNo = subsidiaryLbId.getSelectedItem().getValue().toString();
		}*/

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

	public void onSelect$timeDurLbId() {
		if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			 fromDateboxId.setText("");
			 toDateboxId.setText("");
			 datesDivId.setVisible(true);
		 }
		 else {
			 datesDivId.setVisible(false);
		 }
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
		
		if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 30 Days")) {
			typeDiff = DATE_DIFF_TYPE_DAYS;
			monthsDiff = 30;
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - monthsDiff);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 3 Months")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff = 3;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 6 Months")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff =6;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 1 Year")) {
			typeDiff = DATE_DIFF_TYPE_MONTHS;
			monthsDiff = 12;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			startDate = getStartDate();
			endDate = getEndDate();
			
			if(startDate.get(Calendar.DATE) == endDate.get(Calendar.DATE) && startDate.get(Calendar.MONTH) == endDate.get(Calendar.MONTH)
					&& startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)) {
				typeDiff = DATE_DIFF_TYPE_DAYS;
				return;
			}
	
//			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
			
			/*if(startDate.before(prgmObj.getCreatedDate())) {
				MessageUtil.setMessage("From date should be after the program creation date.", "color:red", "TOP");
				return;
			}*/
			
			int diffDays =  (int) ((endDate.getTime().getTime() - startDate.getTime().getTime() ) / (1000*60*60*24));
			
			int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			monthsDiff = (   
					(endDate.get(Calendar.YEAR)*12 +endDate.get(Calendar.MONTH)) - (startDate.get(Calendar.YEAR)*12 + startDate.get(Calendar.MONTH)))+1;
			
			if(diffDays >= maxDays ) {
				if(monthsDiff > 12) {
					typeDiff = DATE_DIFF_TYPE_YEARS;
					monthsDiff = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR) + 1;
				}
				else {
					typeDiff = DATE_DIFF_TYPE_MONTHS;
				}
			}
			else {
				typeDiff = DATE_DIFF_TYPE_DAYS;
				monthsDiff = diffDays;
			}
			
			
			
		}else{
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		
		}
		
		
		logger.info("str endDate 2"+ startDate + " "+endDate);
		
//		startDateStr = MyCalendar.calendarToString(startDate, null);
//		endDateStr = MyCalendar.calendarToString(endDate, null);
	}
	
	public Calendar getStartDate(){
		
		if(fromDateboxId.getValue() != null && !fromDateboxId.getValue().toString().trim().isEmpty()) {
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, 
				serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		
		return serverFromDateCal;
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			return null;
		}
	}
	
	public Calendar getEndDate() {
		
		if(toDateboxId.getValue() != null && !toDateboxId.getValue().toString().trim().isEmpty()) {
		Calendar serverToDateCal = toDateboxId.getServerValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);
		
		return serverToDateCal;
		}
		else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			return null;
		}
		
	}
	
	private void resetBreakTierCols() {
		Components.removeAllChildren(tierbreakColsId);

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			Column tierName = new Column("Tier Name (Tier Level)");
			tierName.setWidth("40%");
			tierName.setParent(tierbreakColsId);
			/*Column upgradedCount = new Column("Upgraded");
			upgradedCount.setWidth("30%");
			upgradedCount.setParent(tierbreakColsId);*/
			Column totmemberships = new Column("Total Memberships");
			totmemberships.setWidth("60%");
			totmemberships.setParent(tierbreakColsId);

		}
		else {
			Column tierName = new Column("Tier Name (Tier Level)");
			tierName.setWidth("40%");
			tierName.setParent(tierbreakColsId);
			/*Column enrolledCount = new Column("Enrolled");
			enrolledCount.setWidth("40%");
			enrolledCount.setParent(tierbreakColsId);
			Column upgradedCount = new Column("Upgraded");
			upgradedCount.setWidth("30%");
			upgradedCount.setParent(tierbreakColsId);*/
			Column totmemberships = new Column("Total Memberships");
			totmemberships.setWidth("60%");
			totmemberships.setParent(tierbreakColsId);
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
				if (org.getHomeStoreId().equalsIgnoreCase(obj[0].toString()) && (org.getSubsidiaryId().equalsIgnoreCase(obj[1].toString()))) {
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


	private void setEmployeeIds() {
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		logger.info("orgId"+orgId);
		List<String> employeeIdList = ltyPrgmSevice.getAllempIds(orgId);
		logger.info("employeeIdList"+employeeIdList);
		if(employeeIdList == null || employeeIdList.size() == 0) return;
		for (String eachempId : employeeIdList){
		Listitem li = new Listitem(eachempId,eachempId);
			li.setParent(employeeLbId);
		}
		}


	
	private void setCardSets() {
		List<LoyaltyCardSet> cardsetList = ltyPrgmSevice.getCardsetList(prgmId);
		logger.info("cardsetList"+cardsetList);
		if(cardsetList == null || cardsetList.size() == 0) return;
		for (LoyaltyCardSet eachCardset : cardsetList){
			Listitem li = new Listitem(eachCardset.getCardSetName(),eachCardset.getCardSetId());
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
	
	
	private void redraw(List<Object[]> contactLtyList) {
        logger.info("**************************List Size :"+contactLtyList.size());
		MessageUtil.clearMessage();
		DecimalFormat f = new DecimalFormat("#0.00");
		int count =  regReportLbId.getItemCount();

		for(; count>0; count--) {
			regReportLbId.removeItemAt(count-1);
		}

		//System.gc();

		if(contactLtyList == null) return;
		if(contactLtyList != null && contactLtyList.size() > 0) {
			logger.info("setting listitems");
			Listitem li;
			Listcell lc;
			for (Object[] objArr : contactLtyList) {
				SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				Date date = null;
				try {
					date = (Date)formatter.parse(objArr[3].toString().trim());
				} catch (ParseException e) {
					logger.error("Exception ::" , e);
				} 
				Calendar cal=Calendar.getInstance();
				cal.setTime(date);
				String createdDate = "";
				createdDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
				Long cid = Long.parseLong(objArr[4].toString());
				Contacts contacts = ltyPrgmSevice.getContactObj(cid);
			
				li = new Listitem();

				lc = new Listcell();
				String membershipNo=objArr[0] == null ? "" :objArr[0].toString();
				lc.setLabel(membershipNo);
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(membershipNo);
				lc.setParent(li);

				lc = new Listcell();
				lc.setLabel(objArr[1] == null ? "" : objArr[1].toString());
				lc.setTooltiptext(objArr[1] == null ? "" : objArr[1].toString());
				lc.setParent(li);
				
				lc = new Listcell();
				String optinSource = "";
				if(objArr[2] == null) optinSource = "";
				else if(objArr[2].toString().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) optinSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
				else optinSource = objArr[2].toString();
				lc.setLabel(optinSource);
				lc.setTooltiptext(optinSource);
				lc.setParent(li);

				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(createdDate);
				lc.setParent(li);

				
				
			/*	lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(contacts.getFirstName() == null ? "" :contacts.getFirstName());
				lc.setTooltiptext(contacts.getFirstName() == null ? "" :contacts.getFirstName());
				lc.setParent(li);
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(contacts.getLastName() == null ? "":contacts.getLastName());
				lc.setTooltiptext(contacts.getLastName() == null ? "":contacts.getLastName());
				lc.setParent(li);
				
				//Long contactId = contactsLoyalty.getContact().
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(contacts.getMobilePhone() == null ?"":contacts.getMobilePhone());
				lc.setTooltiptext(contacts.getMobilePhone() == null ?"":contacts.getMobilePhone());
				lc.setParent(li);
				

				lc = new Listcell();
				lc.setStyle("padding-left:5px;");
				lc.setLabel(contacts.getEmailId() == null ? "":contacts.getEmailId());
				lc.setTooltiptext(contacts.getEmailId() == null ? "":contacts.getEmailId());
				lc.setParent(li);*/
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(objArr[17] == null ? "" :objArr[17].toString());
				lc.setTooltiptext(objArr[17] == null ? "" :objArr[17].toString());
				lc.setParent(li);
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(objArr[18] == null ? "":objArr[18].toString());
				lc.setTooltiptext(objArr[18] == null ? "":objArr[18].toString());
				lc.setParent(li);
				
				//Long contactId = contactsLoyalty.getContact().
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(objArr[19]== null ?"":objArr[19].toString());
				lc.setTooltiptext(objArr[19] == null ?"":objArr[19].toString());
				lc.setParent(li);
				

				lc = new Listcell();
				lc.setStyle("padding-left:5px;");
				lc.setLabel(objArr[20] == null ? "": objArr[20].toString());
				lc.setTooltiptext(objArr[20] == null ? "":objArr[20].toString());
				lc.setParent(li);

				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(objArr[5] == null ? 0+"":objArr[5].toString());
				lc.setTooltiptext(objArr[5] == null ? 0+"":objArr[5].toString());
				lc.setParent(li);
				
				
				String balanceStr = "";
				if(objArr[9] != null && !objArr[9].toString().isEmpty() && Double.parseDouble(objArr[9].toString()) != 0.0
				   && objArr[6] != null && !objArr[6].toString().isEmpty() && Double.parseDouble(objArr[6].toString()) != 0.0) {
					balanceStr = "Gift : " + f.format(objArr[9]) +" Loyalty : "+ f.format(objArr[6]);
				}else if((objArr[9] == null || Double.parseDouble(objArr[9].toString()) == 0.0) 
						&& objArr[6] != null && !objArr[6].toString().isEmpty()){
					balanceStr = f.format(objArr[6]);
				}else if(objArr[9] != null && !objArr[9].toString().isEmpty()
						  && (objArr[6] == null || Double.parseDouble(objArr[6].toString()) == 0.0)){
					balanceStr = f.format(objArr[9]);
				}else {
					balanceStr = f.format(0.00)+"";
				}
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(balanceStr);
				lc.setTooltiptext(balanceStr);
				lc.setParent(li);
				
				
				String storeName = ""; //"Store ID "+resultSet.getString("pos_location_id") .toString() ;
				String sbsName = "";//"Subsidiary ID "+resultSet.getString("subsidiary_number")  ;
				if(objArr[14] != null){
					sbsName = "Subsidiary ID "+objArr[14].toString() ;
					for (OrganizationStores org : storeList){
						if (objArr[14].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
							sbsName = org.getSubsidiaryName().toString();
							break;
						}}
				}
				if(objArr[7] != null){
					storeName = "Store ID "+objArr[7].toString() ;
					for (OrganizationStores org : storeList){
						if (objArr[7].toString().equalsIgnoreCase(org.getHomeStoreId()) && 
								org.getStoreName() != null && !org.getStoreName().isEmpty()) {
							if(org.getSubsidiaryId() != null && !org.getSubsidiaryId().isEmpty() && 
									objArr[14] != null  && 
											objArr[14].toString().equalsIgnoreCase(org.getSubsidiaryId())){
								
								storeName = org.getStoreName().toString();
								break;
								
							}
							
						}}
				}
				
				
				
				
				/*lc = new Listcell();
				if(objArr[14] != null){
					String subName = "Subsidiary ID "+objArr[14].toString() ;
					for (OrganizationStores org : storeList){
						if (objArr[14].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
							subName = org.getSubsidiaryName().toString();
							break;
						}}
					lc.setLabel(subName);
					lc.setTooltiptext(subName);
				}else{
					lc.setLabel("");
					lc.setTooltiptext("");
				}
				lc.setParent(li);
				
				lc = new Listcell();
				if(objArr[7] != null){
					String sName = "Store ID "+objArr[7].toString() ;
					for (OrganizationStores org : storeList){
						if (objArr[7].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
					    	sName = org.getStoreName().toString();
					    	break;
						}}
							lc.setLabel(sName);
							lc.setTooltiptext(sName);
						}else{
							lc.setLabel("");
							lc.setTooltiptext("");
							}
				lc.setParent(li);*/
				lc = new Listcell();
				lc.setLabel(sbsName);
				lc.setTooltiptext(sbsName);
				
				lc.setParent(li);
				
				lc = new Listcell();
				
				lc.setLabel(storeName);
				lc.setTooltiptext(storeName);
						
				lc.setParent(li);
				String cardType = "" ;
				if(objArr[8] != null) {
					
				
				if(objArr[8].toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
					cardType = "Gift";
				}else if(objArr[8].toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L)){
					cardType = "Loyalty";
				}else if(objArr[8].toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {
					cardType = "Gift & Loyalty";
				}
				}
				
				lc = new Listcell();
				lc.setLabel(cardType);
				lc.setTooltiptext(cardType);
				lc.setParent(li);
				
				String holdBalanceStr = "";
				if(objArr[10] != null && !objArr[10].toString().isEmpty() && Long.parseLong(objArr[10].toString()) != 0
						&& objArr[11] != null && !objArr[11].toString().isEmpty() && Double.parseDouble(objArr[11].toString()) != 0.0) {
					holdBalanceStr = "$ "+f.format(objArr[11])+" & "+objArr[10]+" Points";
				}else if((objArr[11] == null || Double.parseDouble(objArr[11].toString()) == 0.0 || objArr[11].toString().isEmpty()) 
						&& objArr[10] != null && !objArr[10].toString().isEmpty() && Long.parseLong(objArr[10].toString()) != 0){
					holdBalanceStr = objArr[10]+" Points";
				}else if((objArr[10] == null || objArr[10].toString().isEmpty() ||  Long.parseLong(objArr[10].toString()) == 0) 
						&& objArr[11] != null && Double.parseDouble(objArr[11].toString()) != 0.0 && !objArr[11].toString().isEmpty()){
					holdBalanceStr = "$ "+f.format(objArr[11]);
				}else {
					holdBalanceStr = "";
				}
				
                if(!holdBalanceStr.isEmpty() && holdBalanceStr.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(currentUser.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					holdBalanceStr = holdBalanceStr.contains("$")? holdBalanceStr.replace("$", userCurrencySymbol):holdBalanceStr; 
				}
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(holdBalanceStr);
				lc.setTooltiptext(holdBalanceStr);
				lc.setParent(li);
				  
				String lifeTimePoints  = objArr[12] == null ? 0 + ""
							: objArr[12].toString();
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(lifeTimePoints+"");
				lc.setTooltiptext(lifeTimePoints+"");
				lc.setParent(li);
				
				
				
				lc = new Listcell();
				String tierId = objArr[13] == null ? 0 +"" : objArr[13].toString();
				
				Long tier = Long.valueOf(tierId);
				LoyaltyProgramTierDao loyaltyProgramTierDao;
				try {
					loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(tier);
					if(loyaltyProgramTier != null){
					lc.setStyle("padding-left:10px;");
					lc.setLabel(loyaltyProgramTier.getTierType()+"");
					lc.setTooltiptext(loyaltyProgramTier.getTierType()+"");
					lc.setParent(li);
					}else{
		                lc.setStyle("padding-left:10px;");
						lc.setLabel("");
						lc.setTooltiptext("");
						lc.setParent(li);
						
					}
				
					li.setHeight("30px");
					li.setParent(regReportLbId);
					li.setValue(objArr);
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(objArr[15].toString());
				lc.setTooltiptext(objArr[15].toString());
				lc.setParent(li);
				
				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(String.format("%.2f", objArr[16]));
				lc.setTooltiptext(String.format("%.2f", objArr[16]));
				lc.setParent(li);
				}
					
				catch (Exception e) {
					logger.error("Exception",e);
				}
				
				
				
				}
		}


	}

	public void setPlotData() { 
		try{
			logger.debug(" >> before setPlotData time In millis ::"+System.currentTimeMillis());
			
			
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
			
			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
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
			logger.info("setPlotData():::stores::"+storeNo);
			Long cardsetId = null;
			if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
			String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }
			boolean isTransacted = false;
			if(transCardsChkId.isChecked()) {
				isTransacted = true;
			}
			
			logger.debug(" startDateStr :"+MyCalendar.calendarToString(startDate, null)+ " AND endDateStr Str ::"+MyCalendar.calendarToString(endDate, null));
			
			CategoryModel model = new SimpleCategoryModel();
			
			/*List<Object[]>  regRates = ltyPrgmSevice.k(userId, prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),storeNo, cardsetId,isTransacted,typeDiff,"Loyalty",employeeIdStr,tierId);*/
			List<Object[]>  regRates = ltyPrgmSevice.findTotRegistrationsRate(userId, prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),subsidiaryNo, storeNo, cardsetId,isTransacted,typeDiff,"Loyalty",employeeIdStr,tierId);
			Map<String, Integer> regMap = null;
			if(regRates != null && regRates.size() > 0){
				regMap = new HashMap<String, Integer>();
				for (Object[] obj : regRates) {
					regMap.put(obj[2].toString(), Integer.parseInt(obj[0].toString()));
				}
			}
			
			Calendar tempCal=Calendar.getInstance();
			tempCal.setTimeZone(clientTimeZone);
			tempCal.setTimeInMillis(startDate.getTimeInMillis());
			String currDate = "";
			
			if(typeDiff.equalsIgnoreCase(DATE_DIFF_TYPE_DAYS)) {
				plot1.setXAxis("Days");
				do {
					currDate = ""+tempCal.get(startDate.DATE);
					if(regMap != null) {
						model.setValue("No. of Enrollments", currDate, regMap.containsKey(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) ? regMap.get(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) : 0);
					}
					else {
						model.setValue("No. of Enrollments", currDate, 0);
					}
					tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
				}while(tempCal.before(endDate) || tempCal.equals(endDate));
			}
			
			
			else if(typeDiff.equals(DATE_DIFF_TYPE_MONTHS)) {
				 plot1.setXAxis("Months");
				 int i=1;
				 do {
					 i++;

					 currDate = ""+(tempCal.get(startDate.MONTH)+1);
					 if(regMap != null) {
						 model.setValue("No. of Enrollments", MONTH_MAP.get(currDate), regMap.containsKey(currDate) ? regMap.get(currDate) : 0);
					 }
					 else {
							model.setValue("No. of Enrollments", MONTH_MAP.get(currDate), 0);
					 }

					 tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

				 } while(i<= monthsDiff);
			 }
			
			else if(typeDiff.equals(DATE_DIFF_TYPE_YEARS)) {
				 plot1.setXAxis("Years");
				 int i=1;
				 do {
					 i++;

					 currDate = ""+(tempCal.get(startDate.YEAR));
					 if(regMap != null) {
						 model.setValue("No. of Enrollments", currDate, regMap.containsKey(currDate) ? regMap.get(currDate) : 0);
					 }
					 else {
							model.setValue("No. of Enrollments", currDate, 0);
					 }
					 tempCal.set(Calendar.YEAR, tempCal.get(Calendar.YEAR) + 1);

				 } while(i<= monthsDiff);
			 }

			
			if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				
				/*List<Object[]>  gftRegRates = ltyPrgmSevice.findTotRegistrationsRate(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),storeNo, cardsetId,isTransacted,typeDiff,"Gift",employeeIdStr,tierId);*/
				/*List<Object[]>  gftRegRates = ltyPrgmSevice.findTotRegistrationsRate(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),subsidiaryNo, storeNo, cardsetId,isTransacted,typeDiff,"Gift",employeeIdStr,tierId);
				*/logger.debug("-------- registrations is ::::"+regRates.size());
				
				/*if(gftRegRates == null || gftRegRates.size()==0) {
				}*/

			
				Map<String, Integer> gftRegMap = null;
				if(regRates != null && regRates.size() > 0){
					gftRegMap = new HashMap<String, Integer>();
					for (Object[] obj : regRates) {
						gftRegMap.put(obj[2].toString(), Integer.parseInt(obj[1].toString()));
					}
				}
				
				tempCal=Calendar.getInstance();
				tempCal.setTimeZone(clientTimeZone);
				tempCal.setTimeInMillis(startDate.getTimeInMillis());
				currDate = "";
				
				if(typeDiff.equalsIgnoreCase(DATE_DIFF_TYPE_DAYS)) {
					plot1.setXAxis("Days");
					do {
						currDate = ""+tempCal.get(startDate.DATE);
						if(gftRegMap != null) {
							model.setValue("No. of Gift Issuances", currDate, gftRegMap.containsKey(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) ? gftRegMap.get(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) : 0);
						}
						else {
							model.setValue("No. of Gift Issuances", currDate, 0);
						}
						tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
					}while(tempCal.before(endDate) || tempCal.equals(endDate));
				}
				
				
				else if(typeDiff.equals(DATE_DIFF_TYPE_MONTHS)) {
					 plot1.setXAxis("Months");
					 int i=1;
					 do {
						 i++;

						 currDate = ""+(tempCal.get(startDate.MONTH)+1);
						 if(gftRegMap != null) {
							 model.setValue("No. of Gift Issuances", MONTH_MAP.get(currDate), gftRegMap.containsKey(currDate) ? gftRegMap.get(currDate) : 0);
						 }
						 else {
							 model.setValue("No. of Gift Issuances", MONTH_MAP.get(currDate), 0);
						 }

						 tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

					 } while(i<= monthsDiff);
				 }
				
				else if(typeDiff.equals(DATE_DIFF_TYPE_YEARS)) {
					 plot1.setXAxis("Years");
					 int i=1;
					 do {
						 i++;

						 currDate = ""+(tempCal.get(startDate.YEAR));
						 if(gftRegMap != null) {
							 model.setValue("No. of Gift Issuances", currDate, gftRegMap.containsKey(currDate) ? gftRegMap.get(currDate) : 0);
						 }
						 else {
							 model.setValue("No. of Gift Issuances", currDate, 0);
						 }
						 tempCal.set(Calendar.YEAR, tempCal.get(Calendar.YEAR) + 1);

					 } while(i<= monthsDiff);
				 }

			}
			plot1.setModel(model);

			logger.debug(" >> after setPlotData time In millis ::"+System.currentTimeMillis());
		} catch(Exception e) {
			logger.debug(" Exception : while generating the line chart ",(Throwable)e);
		}
	} // setPlotData

	public void onSelect$pageSizeLbId(){

		try {
			
			String key = cardSearchBoxId.getValue();
			
			
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
			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
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
			if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
			boolean isTransacted = false;
			if(transCardsChkId.isChecked()) {
				isTransacted = true;
			}
			String status = null;
		    if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
			status = statusLbId.getSelectedItem().getValue();
				}
		    String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }
				int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),key, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				loyaltyListBottomPagingId.setTotalSize(totalSize);
				int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				loyaltyListBottomPagingId.setPageSize(pageSize);
				loyaltyListBottomPagingId.setActivePage(0);
			
				List<Object[]> contactLtyList = null;
				/*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),	loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
				contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),	loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),key,subsidiaryNo,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				redraw(contactLtyList);
		} catch (Exception e) {
			logger.error("Exception");
		} 
	
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Paging) {
			String key= cardSearchBoxId.getValue();
			Paging paging = (Paging)event.getTarget();

			int desiredPage = paging.getActivePage();
			this.loyaltyListBottomPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
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
			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
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
			if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
			boolean isTransacted = false;
			if(transCardsChkId.isChecked()) {
				isTransacted = true;
			}
			String status = null;
			if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
			status = statusLbId.getSelectedItem().getValue();
				}
			 String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }
				List<Object[]> contactLtyList = null;
				/*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), ofs, pSize,null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
				contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), ofs, pSize,key, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				redraw(contactLtyList);
			
		}//if
	}
	
	
	 public void onChanging$cardSearchBoxId(InputEvent event) {
			
		 String key = event.getValue();
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
		for (Listitem item : storeLbId.getSelectedItems()) 
	        {
			  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if(storeNo.length() == 0) {
					storeNo = storeNo + "'"+item.getValue()+ "'" ;
					}
					else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
						
					}
				}
	        }
		/* for (Listitem item : storeLbId.getSelectedItems()) {
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
			if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			}
			Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
			boolean isTransacted = false;
			if(transCardsChkId.isChecked()) {
				isTransacted = true;
			}
		    String status = null;
			if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
			status = statusLbId.getSelectedItem().getValue();
				}
			 String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }

				 int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId, MyCalendar.calendarToString(startDate, null),
							MyCalendar.calendarToString(endDate, null),key, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				loyaltyListBottomPagingId.setTotalSize(totalSize);
				int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				loyaltyListBottomPagingId.setPageSize(pageSize);
				loyaltyListBottomPagingId.setActivePage(0);
			
				List<Object[]> contactLtyList = null;
				/*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),	loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
				contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),	loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),key,subsidiaryNo,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				redraw(contactLtyList);
				

		}//onChanging$emailSearchBoxId
	 
	 
	 public void onClick$cardResetBtnId() {

		 try {

			 cardSearchBoxId.setValue("");
			 statusLbId.setSelectedIndex(1);
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

			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
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
				if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
				}
				Long tierId = null;
				if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
				}
				boolean isTransacted = false;
				if(transCardsChkId.isChecked()) {
					isTransacted = true;
				}
			   String status = null;
			   if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
			   status = statusLbId.getSelectedItem().getValue();
					}
			   String employeeIdStr = Constants.STRING_NILL;
				 for (Listitem item : employeeLbId.getSelectedItems()) 
			        {
					  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
						if(employeeIdStr.length() == 0) {
							employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
							}
							else {
								employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
								}
						}
					 }
			 int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null),null, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
			 loyaltyListBottomPagingId.setTotalSize(totalSize);
			 int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			 loyaltyListBottomPagingId.setPageSize(pageSize);
			 loyaltyListBottomPagingId.setActivePage(0);

			 List<Object[]> contactLtyList = null;
			 /*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
			 contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
						MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
						loyaltyListBottomPagingId.getPageSize(),null,subsidiaryNo,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
			 redraw(contactLtyList);
		 } catch (WrongValueException e) {
			 logger.error("Exception ::" , e);
		 } catch (Exception e) {
			 logger.error("Exception ::" , e);
		 }
	 }//onClick$cardResetBtnId()
	    
	 
	 public void onClick$exportBtnId() {
		 	createWindow();
			//anchorEvent(false);
			
			custExport.setVisible(true);
			custExport.doHighlighted();
		
		} //onClick$exportLblId
	 public void onSelect$statusLbId(){
		 cardSearchBoxId.setValue("");
		 
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

		 for (Listitem item : storeLbId.getSelectedItems()) 
	        {
			  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if(storeNo.length() == 0) {
					storeNo = storeNo + "'"+item.getValue()+ "'" ;
					}
					else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
						
					}
				}
	        }
		/* for (Listitem item : storeLbId.getSelectedItems()) {
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
			 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				 cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			 }
			 Long tierId = null;
				if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
				}
			 boolean isTransacted = false;
				if(transCardsChkId.isChecked()) {
					isTransacted = true;
				}
		    String status = null;
			if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
				status = statusLbId.getSelectedItem().getValue();
			}
			 String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }

		
		 int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),null, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);

			loyaltyListBottomPagingId.setTotalSize(totalSize);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			loyaltyListBottomPagingId.setPageSize(pageSize);
			loyaltyListBottomPagingId.setActivePage(0);
			loyaltyListBottomPagingId.addEventListener("onPaging", this); 
			List<Object[]> contactLtyList = null;
			/*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null), 0,
					loyaltyListBottomPagingId.getPageSize(),null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
			contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null), 0,
					loyaltyListBottomPagingId.getPageSize(),null,subsidiaryNo,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
			
			redraw(contactLtyList);
	 }
	 
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
		 if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
		 if(!isValidate()) {
			 return;
		 }
		 }
		 
		 getDateValues();
		 tierbreakLblId.setValue(MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - " +
				 MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));

		 plotLblId.setValue(MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - " +
				 MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));

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

		 for (Listitem item : storeLbId.getSelectedItems()) 
	        {
			  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if(storeNo.length() == 0) {
					storeNo = storeNo + "'"+item.getValue()+ "'" ;
					}
					else {
						storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
						
					}
				}
	        }
		/* for (Listitem item : storeLbId.getSelectedItems()) {
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
		 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			 cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
		 }
		 Long tierId = null;
			if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
			}
		 boolean isTransacted = false;
			if(transCardsChkId.isChecked()) {
				isTransacted = true;
			}
		String status = null;
		if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
		status = statusLbId.getSelectedItem().getValue();
			}
		String employeeIdStr = Constants.STRING_NILL;
		 for (Listitem item : employeeLbId.getSelectedItems()) 
	        {
			  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
				if(employeeIdStr.length() == 0) {
					employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
					}
					else {
						employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
						}
				}
			 }

		 plot1.setEngine(new LineChartEngine());
		 this.setPlotData();
		 


		 int totalSize = ltyPrgmSevice.getRegistrationsCount(userId, prgmId,MyCalendar.calendarToString(startDate, null),
				 MyCalendar.calendarToString(endDate, null),null, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);

		 loyaltyListBottomPagingId.setTotalSize(totalSize);
		 int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		 loyaltyListBottomPagingId.setPageSize(pageSize);
		 loyaltyListBottomPagingId.setActivePage(0);
		 loyaltyListBottomPagingId.addEventListener("onPaging", this); 
		 List<Object[]> contactLtyList = null;
		 /*contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
				 MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
				 loyaltyListBottomPagingId.getPageSize(),null,storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);*/
		 contactLtyList =ltyPrgmSevice.getContactLtyList(userId, prgmId, MyCalendar.calendarToString(startDate, null),
				 MyCalendar.calendarToString(endDate, null), loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(),
				 loyaltyListBottomPagingId.getPageSize(),null, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
		 /*redrawTierSummary(prgmId,MyCalendar.calendarToString(startDate, null),
				 MyCalendar.calendarToString(endDate, null),isTransacted,storeNo,cardsetId,employeeIdStr,tierId);*/
		 redrawTierSummary(prgmId,MyCalendar.calendarToString(startDate, null),
				 MyCalendar.calendarToString(endDate, null),isTransacted,subsidiaryNo,storeNo,cardsetId,employeeIdStr,tierId);
		 redraw(contactLtyList);
	 }
	 
	 private boolean isValidate() {

		 if(fromDateboxId.getValue() != null && !fromDateboxId.getValue().toString().isEmpty()) {
			 startDate = MyCalendar.getNewCalendar();
			 startDate.setTime(fromDateboxId.getValue());
			 logger.debug("startDate ::"+MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_DATETIME_STYEAR));
		 }
		 else {
			 MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			 return false;
		 }

		 if(startDate == null) {
			 return false;
		 }

		 if(toDateboxId.getValue() != null && !toDateboxId.getValue().toString().isEmpty()) {
			 endDate = MyCalendar.getNewCalendar();
			 endDate.setTime(toDateboxId.getValue());
			 logger.debug("endDate ::"+MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_DATETIME_STYEAR));
		 }
		 else{
			 MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			 return false;
		 }

		 if(endDate == null) {
			 return false;
		 }
		 if(endDate.before(startDate)) {
			 MessageUtil.setMessage("To date must be later than From date.", "color:red", "TOP");
			 return false;
		 }

		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 if(!sdf.format(startDate.getTime()).equals(sdf.format(prgmObj.getCreatedDate().getTime()))){
			 if(startDate.before(prgmObj.getCreatedDate())) {
					 MessageUtil.setMessage("From date should be after the program creation date.", "color:red", "TOP");
					 return false;
			 }
		 }
		 return true;

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
			 if(!(chkList.get(i) instanceof Checkbox)) continue;

			 tempChk = (Checkbox)chkList.get(i);
			 tempChk.setChecked(flag);

		 } // for

	 }



	 public void onClick$selectFieldBtnId$custExport() {

		 custExport.setVisible(false);
		 List<Component> chkList = custExport$chkDivId.getChildren();

		 int indexes[]=new int[chkList.size()];
		 
		 logger.info("indexes[]"+chkList.size());
		 boolean checked=false;

		 for(int i=0;i<chkList.size();i++) {
			 indexes[i]=-1;
		 } // for


		 Checkbox tempChk = null;


		 for (int i = 0; i < chkList.size(); i++) {
			 if(!(chkList.get(i) instanceof Checkbox)) continue;

			 tempChk = (Checkbox)chkList.get(i);

			 if(tempChk.isChecked()) {
				 indexes[i]=0;
				 checked=true;
			 }else{
					indexes[i]=-1;
				}

		 } // for


		 if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

			 checked=true;
		 }

		 if(checked) {

			 int confirm=Messagebox.show("Do you want to export with selected field(s) ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			 if(confirm==1){
				 try{

					 exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);

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
	 private void exportCSV(String value, int[] indexes) {

		 logger.debug("-- just entered fileDownload--");
		 StringBuffer sb = null;
		 String userName = GetUser.getUserName();
		 String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		 String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		 File downloadDir = new File(exportDir);
		 if(downloadDir.exists()){
			 try {
				 FileUtils.deleteDirectory(downloadDir);
				 logger.debug(downloadDir.getName() + " is deleted");
			 } catch (Exception e) {
				 logger.error("Exception ::" , e);

				 logger.debug(downloadDir.getName() + " is not deleted");
			 }
		 }
		 if(!downloadDir.exists()){
			 downloadDir.mkdirs();
		 }
			 String prgmName = prgmObj.getProgramName();
			 if(prgmName.contains("/")) {

				 prgmName = prgmName.replace("/", "_") ;

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

			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
						}
					}
		        }
			 Long cardsetId = null;
			 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				 cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			 }
			 Long tierId = null;
				if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
				}
			 boolean isTransacted = false;
			 if(transCardsChkId.isChecked()) {
				 isTransacted = true;
			 }
			String status = null;
			if(!statusLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")){
			status = statusLbId.getSelectedItem().getValue();
				}
			String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }

             String key = cardSearchBoxId.getValue();
			 try{

			 String filePath = exportDir +  "Loyalty_Report_" + prgmName + "_" +
					 MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
				 DecimalFormat f = new DecimalFormat("#0.00");
				 filePath = filePath + "_Enrollments.csv";
				 logger.debug("Download File path : " + filePath);
				 File file = new File(filePath);
				 BufferedWriter bw;
					bw = new BufferedWriter(new FileWriter(filePath));
				 int count ;
						 count = ltyPrgmSevice.getRegistrationsCount(userId, prgmId, MyCalendar.calendarToString(startDate, null),
							 MyCalendar.calendarToString(endDate, null), key.trim(), subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
				
				 if(count == 0) {
					 Messagebox.show("No enrollments found with the given search criteria.",
							 "Info", Messagebox.OK,Messagebox.INFORMATION);
					 return;
				 }
				 String udfFldsLabel= "";
				 String dummyColumn="";
				 if(indexes[0]==0) {
					 udfFldsLabel = "\""+"Membership Number"+"\""+",";
					 dummyColumn+=",address_one";
				 }
				 if(indexes[1]==0) {
					 udfFldsLabel += "\""+"Membership Status"+"\""+",";
					 dummyColumn+=",address_two";
				 }
				 if(indexes[2]==0) {
					 udfFldsLabel += "\""+"Reg. Source"+"\""+",";
					 dummyColumn+=",city";
				 }	
				 if(indexes[3]==0) {

					 udfFldsLabel += "\""+"Registered On"+"\""+",";
					 dummyColumn+=",created_date";
				 }
				 if(indexes[4]==0) {

					 udfFldsLabel += "\""+"First Name"+"\""+",";
					 dummyColumn+=",first_name";
				 }
				 if(indexes[5]==0) {

					 udfFldsLabel += "\""+"Last Name"+"\""+",";
					 dummyColumn+=",last_name";
				 }
				 if(indexes[6]==0) {

					 udfFldsLabel += "\""+"Mobile Number"+"\""+",";
					 dummyColumn+=",mobile_phone";
					 
				 }
				 if(indexes[7]==0) {

					 udfFldsLabel += "\""+"Email Address"+"\""+",";
					 dummyColumn+=",email_id";
				 }
				 if(indexes[8]==0) {

					 udfFldsLabel += "\""+"Balance Points"+"\""+",";
					 dummyColumn+=",udf1";
				 }
				 if(indexes[9]==0) {

					 udfFldsLabel += "\""+"Balance Currency"+"\""+",";
					 dummyColumn+=",udf2";
				 }
				 if(indexes[10]==0) {

					 udfFldsLabel += "\""+"Subsidiary"+"\""+",";
					 dummyColumn+=",udf3";
				 }
				 if(indexes[11]==0) {

					 udfFldsLabel += "\""+"Store"+"\""+",";
					 dummyColumn+=",udf4";
				 }
				 if(indexes[12]==0) {

					 udfFldsLabel += "\""+"Card Type"+"\""+",";
					 dummyColumn+=",udf5";
				 }
				 if(indexes[13]==0) {

					 udfFldsLabel += "\""+"Hold Balance"+"\""+",";
					 dummyColumn+=",udf6";
				 }
				 if(indexes[14]==0) {

					 udfFldsLabel += "\""+"Lifetime Points"+"\""+",";
					 dummyColumn+=" ,udf7";
				 }
				 if(indexes[15]==0) {

					 udfFldsLabel += "\""+"Tier"+"\""+",";
					 dummyColumn+=" ,udf8";
				 }
				 if(indexes[16]==0) {

					 udfFldsLabel += "\""+"Total Visits"+"\""+",";
					 dummyColumn+=" ,udf9";
				 }
				 if(indexes[17]==0) {

					 udfFldsLabel += "\""+"Lifetime Purchase Value"+"\""+",";
					 dummyColumn+=" ,udf10";
				 }
				 sb = new StringBuffer();
				 sb.append(udfFldsLabel);
				 sb.append("\r\n");

				 bw.write(sb.toString());
				 
				 String exportQuery="SELECT id "+dummyColumn+" FROM tempexportreport where 1=2";
				 logger.info("Dummy export query :"+exportQuery);

				  String query=null;
					LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					List<LoyaltyProgramTier> listTier = loyaltyProgramTierDao.getAllTierByProgramId(prgmId);
					Map<Long,String> tierMap=new HashMap<>();
					if(listTier!=null && listTier.size()>0)
				  for(LoyaltyProgramTier tier:listTier){
					  if(tier!=null)
					  tierMap.put(tier.getTierId(), tier.getTierType());
				   }
				  
					 sb = new StringBuffer();
						 query = prepareQuery(userId, prgmId, MyCalendar.calendarToString(startDate, null),
								 MyCalendar.calendarToString(endDate, null),  key, subsidiaryNo, storeNo,cardsetId,isTransacted,status,employeeIdStr,tierId);
					
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
							 SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
							 Date date = null;
								 date = (Date)formatter.parse(resultSet.getTimestamp("created_date").toString().trim());
							 Calendar cal=Calendar.getInstance();
							 cal.setTime(date);
							 String createdDate = resultSet.getTimestamp("created_date")==null?"":MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone);
							 String cardType = "" ;
							 if(resultSet.getString("reward_flag") != null) {
								 if(resultSet.getString("reward_flag").toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
									 cardType = "Gift";
								 }else if(resultSet.getString("reward_flag").toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L)){
									 cardType = "Loyalty";
								 }else if(resultSet.getString("reward_flag").toString().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {
									 cardType = "Gift & Loyalty";
								 }
							 }
							 
							 exportresResultSet.updateLong("id",pk++);
							 if(indexes[0]==0) {
								 exportresResultSet.updateString("address_one", resultSet.getString("card_number"));
							 }
							 if(indexes[1]==0) {
								 exportresResultSet.updateString("address_two", resultSet.getString("membership_status"));
							 }
							 if(indexes[2]==0) {
								 String optinSource = "";
								 if(resultSet.getString("contact_loyalty_type")== null) optinSource = "";
								 else if(resultSet.getString("contact_loyalty_type").toString().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) optinSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
								 else optinSource = resultSet.getString("contact_loyalty_type").toString();
								 exportresResultSet.updateString("city",optinSource );
							 }
							 if(indexes[3]==0) {
								 exportresResultSet.updateString("created_date", createdDate);
							 }
							 if(indexes[4]==0) {
								 exportresResultSet.updateString("first_name", resultSet.getString("first_name"));
							 }
							 if(indexes[5]==0) {
								 exportresResultSet.updateString("last_name", resultSet.getString("last_name"));
							 }
							 if(indexes[6]==0) {
								 exportresResultSet.updateString("mobile_phone", resultSet.getString("mobile_phone"));
							 }
							 if(indexes[7]==0) {
								 exportresResultSet.updateString("email_id", resultSet.getString("email_id"));
							 }
							 if(indexes[8]==0) {
								 exportresResultSet.updateString("udf1",resultSet.getString("loyalty_balance"));
							 }
							 String balanceStr = "";
							 if(resultSet.getString("gift_balance") != null && !resultSet.getString("gift_balance").isEmpty() && Double.parseDouble(resultSet.getString("gift_balance")) != 0.0
									 && resultSet.getString("giftcard_balance") != null && !resultSet.getString("giftcard_balance").toString().isEmpty() && Double.parseDouble(resultSet.getString("giftcard_balance")) != 0.0) {
								 balanceStr = "Gift : " + f.format(resultSet.getDouble("gift_balance")) +" Loyalty : "+ f.format(resultSet.getDouble("giftcard_balance"));
							 }else if((resultSet.getString("gift_balance") == null || Double.parseDouble(resultSet.getString("gift_balance")) == 0.0) 
									 && resultSet.getString("giftcard_balance") != null && !resultSet.getString("giftcard_balance").isEmpty()){
								 balanceStr = f.format(resultSet.getDouble("giftcard_balance"));
							 }else if(resultSet.getString("gift_balance") != null && !resultSet.getString("gift_balance").isEmpty()
									 && (resultSet.getString("giftcard_balance") == null || Double.parseDouble(resultSet.getString("giftcard_balance")) == 0.0)){
								 balanceStr = f.format(resultSet.getDouble("gift_balance"));
							 }else {
								 balanceStr = f.format(0.00)+"";
							 }

							 if(indexes[9]==0) {
								 exportresResultSet.updateString("udf2",balanceStr);
							 }
							 /*if(indexes[10]==0) {
								 if(resultSet.getString("subsidiary_number") != null){
										String sName = "Subsidiary ID "+resultSet.getString("subsidiary_number")  ;
										for (OrganizationStores org : storeList){
											if (resultSet.getString("subsidiary_number") .equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
										    	sName = org.getSubsidiaryName().toString();
										    	break;
											}}
										exportresResultSet.updateString("udf3",sName);
											}else{
												exportresResultSet.updateString("udf3","");
												}
							 
								 }
							 if(indexes[11]==0) {
								 if(resultSet.getString("pos_location_id")  != null){
										String sName = "Store ID "+resultSet.getString("pos_location_id") .toString() ;
										for (OrganizationStores org : storeList){
											if (resultSet.getString("pos_location_id") .equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
										    	sName = org.getStoreName().toString();
										    	break;
											}}
										exportresResultSet.updateString("udf4",sName);
											}else{
												exportresResultSet.updateString("udf4","");
												}
								 }*/
							 String sbsName = "";//"Subsidiary ID "+resultSet.getString("subsidiary_number")  ;
							 String storeName = ""; //"Store ID "+resultSet.getString("pos_location_id") .toString() ;
							 if(resultSet.getString("subsidiary_number") != null){
								 sbsName = "Subsidiary ID "+resultSet.getString("subsidiary_number")  ;
									for (OrganizationStores org : storeList){
										if (resultSet.getString("subsidiary_number") .equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
											sbsName = org.getSubsidiaryName().toString();
									    	break;
										}}
							 }
							 if(resultSet.getString("pos_location_id")  != null){
								 storeName = "Store ID "+resultSet.getString("pos_location_id") .toString() ;
									for (OrganizationStores org : storeList){
										if (resultSet.getString("pos_location_id") .equalsIgnoreCase(org.getHomeStoreId()) && 
												org.getStoreName() != null && !org.getStoreName().isEmpty()) {
											if(org.getSubsidiaryId() != null && 
													!org.getSubsidiaryId().isEmpty() && 
													resultSet.getString("subsidiary_number") != null && 
													resultSet.getString("subsidiary_number").equalsIgnoreCase(org.getSubsidiaryId())){
												
												storeName = org.getStoreName().toString();
												break;
											}
										}}
							 }
							 if(indexes[10]==0) {
								 
								exportresResultSet.updateString("udf3",sbsName);
									
										
							 
							 }
							 if(indexes[11]==0) {
								 
								exportresResultSet.updateString("udf4",storeName);
											
							}
							 if(indexes[12]==0) {
								 exportresResultSet.updateString("udf5",cardType);
							 }
							 
							 String holdBalanceStr = "";
							 if(resultSet.getString("holdpoints_balance") != null && !resultSet.getString("holdpoints_balance").isEmpty() && Double.parseDouble(resultSet.getString("holdpoints_balance")) != 0.0
									 && resultSet.getString("holdAmount_balance") != null && !resultSet.getString("holdAmount_balance").isEmpty() && Double.parseDouble(resultSet.getString("holdAmount_balance")) != 0.0) {
								 holdBalanceStr = "$ "+f.format(resultSet.getDouble("holdAmount_balance"))+" & "+resultSet.getDouble("holdpoints_balance")+" Points";
							 }else if((resultSet.getString("holdAmount_balance") == null || Double.parseDouble(resultSet.getString("holdAmount_balance")) == 0.0 || resultSet.getString("holdAmount_balance").isEmpty()) 
									 && resultSet.getString("holdpoints_balance") != null && !resultSet.getString("holdpoints_balance").isEmpty() && Double.parseDouble(resultSet.getString("holdpoints_balance")) != 0.0){
								 holdBalanceStr =resultSet.getString("holdpoints_balance")+" Points";
							 }else if((resultSet.getString("holdpoints_balance") == null || resultSet.getString("holdpoints_balance").isEmpty() || Double.parseDouble(resultSet.getString("holdpoints_balance")) == 0.0)
									 && resultSet.getString("holdAmount_balance") != null && Double.parseDouble(resultSet.getString("holdAmount_balance")) != 0.0 && !resultSet.getString("holdAmount_balance").isEmpty()){
								 holdBalanceStr = "$ "+f.format(resultSet.getDouble("holdAmount_balance"));
							 }else {
								 holdBalanceStr = "";
							 }
							 if(indexes[13]==0) {
								 exportresResultSet.updateString("udf6",holdBalanceStr);
							 }
							 String lifeTimePoints  =resultSet.getString("total_loyalty_earned") == null ? 0 + "" : resultSet.getString("total_loyalty_earned");
							 if(indexes[14]==0) {
								 exportresResultSet.updateString("udf7",lifeTimePoints);
							 }
							 if(indexes[15]==0){
								 exportresResultSet.updateString("udf8",tierMap.get(resultSet.getLong("program_tier_id")));
							 }
								if(indexes[16]==0){
									exportresResultSet.updateString("udf9", resultSet.getString("numberOfVisits")!=null?resultSet.getString("numberOfVisits"):"0");
								}
								if(indexes[17]==0){
									exportresResultSet.updateString("udf10", resultSet.getString("LPV")!=null?resultSet.getString("LPV"):"0.00");
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
							csvWriter = null;
							resultSet=null;
							exportresResultSet=null;
						}
							Filedownload.save(file, "text/csv");
						
						}
						catch (Exception e) {
							logger.info("Exception ", e);
						}
						logger.debug("exited");
					
			}
	private String prepareQuery(Long userId, Long prgmId,
			String startDateStr, String endDateStr,String key, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		String subQry = "";
		if(key != null){
			subQry += " AND cl.card_number LIKE '%"+key.trim()+"%'";
		}
		if(subsidiaryNo != null && subsidiaryNo.length() != 0) {
			subQry += " AND cl.subsidiary_number in ("+subsidiaryNo+")";
		}
		if(storeNo != null && storeNo.length() != 0) {
			subQry += " AND cl.pos_location_id in ("+storeNo+")";
		}
		if(cardsetId != null) {
			subQry += " AND cl.card_set_id =" + cardsetId.longValue();
		}
		if(tierId != null) {
			subQry += " AND cl.program_tier_id =" + tierId.longValue();
		}
        if(status != null) {
        	subQry += " AND cl.membership_status = '"+ status +"'";
        }
        if(employeeIdStr != null && employeeIdStr.length() != 0) {
        	subQry += " AND cl.emp_id in ("+employeeIdStr+")";
        }
		String query = "";
		if(isTransacted) {
			query="SELECT  cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id ,cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
	                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,tc.lifetimepurchase, (if(cl.cummulative_purchase_value>0,(cl.cummulative_purchase_value-(if(cl.cummulative_return_value>=0, cl.cummulative_return_value,0))), 0)) as LPV "
	                   + " FROM contacts_loyalty cl, contacts cc ,(SELECT loyalty_id, count(distinct docsid) as numberOfVisits,SUM(if(transaction_type = 'Issuance' AND entered_amount_type = 'Purchase',entered_amount, 0)) as lifetimepurchase"
	                   +" FROM loyalty_transaction_child WHERE user_id= "+ userId.longValue()+" AND program_id ="+ prgmId.longValue()+" AND transaction_type !='Enrollment'  GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1 ) tc"
	                  +" WHERE cl.contact_id=cc.cid  and cl.loyalty_id = tc.loyalty_id AND cl.user_id= " + userId.longValue() + subQry+" AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +" ' ORDER BY cl.created_date DESC";

		}
		else {
			query="SELECT cl.card_number, cl.membership_status,  cl.contact_loyalty_type, cl.created_date, cl.contact_id,cl.loyalty_balance, cl.giftcard_balance,cl.pos_location_id, cl.reward_flag,cl.gift_balance,"
	                   +"cl.holdpoints_balance, cl.holdAmount_balance,cl.total_loyalty_earned,cl.program_tier_id,cl.subsidiary_number,tc.numberOfVisits,tc.lifetimepurchase  ,cc.first_name,cc.last_name,cc.mobile_phone,cc.email_id,(if(cl.cummulative_purchase_value>0,(cl.cummulative_purchase_value-(if(cl.cummulative_return_value>=0, cl.cummulative_return_value,0))), 0)) as LPV "
	                   + " FROM contacts cc , contacts_loyalty cl  LEFT JOIN(SELECT loyalty_id, count(distinct docsid) as numberOfVisits,SUM(if(transaction_type = 'Issuance' AND entered_amount_type = 'Purchase',entered_amount, 0)) as lifetimepurchase"
	                   +" FROM loyalty_transaction_child WHERE user_id= "+ userId.longValue()+" AND program_id ="+ prgmId.longValue()+" GROUP BY loyalty_id HAVING  COUNT(loyalty_id) >= 1) tc  on cl.loyalty_id = tc.loyalty_id"
	                  +" WHERE cl.contact_id=cc.cid   AND cl.user_id= " + userId.longValue() +" AND cl.program_id ="+ prgmId.longValue()+ subQry+"   AND cl.created_date >= '" +startDateStr +"' AND cl.created_date <='"+ endDateStr +" ' ORDER BY cl.created_date DESC";


		}
		return query;
	}
	public void createWindow()	{

		try {

			Components.removeAllChildren(custExport$chkDivId);

			Checkbox tempChk2 = new Checkbox("Membership Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			tempChk2 = new Checkbox("Membership Status");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			tempChk2 = new Checkbox("Reg. Source");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Registered On");
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
			
			tempChk2 = new Checkbox("Balance Points");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Balance Currency");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Subsidiary Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Store Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Card Type");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Hold Balance");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Lifetime Points");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Tier");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Total Visits");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Lifetime Purchase Value");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
public void onClick$exporttierBtnId(){
		
		logger.debug("-- just entered --");
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String type = (String)exportCbId.getSelectedItem().getValue();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		BufferedWriter bw = null;
		if(tierbreakRowsId.getChildren().size() == 0){
			MessageUtil.setMessage("No records found to export", "color:red", "TOP");
			return;
		}
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				
				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		if(type.contains("csv")){
			
			DecimalFormat f = new DecimalFormat("#0.00");
			String prgmName = prgmObj.getProgramName();
			if(prgmName.contains("/")) {
				
				prgmName = prgmName.replace("/", "_") ;
				
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

			 for (Listitem item : storeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(storeNo.length() == 0) {
						storeNo = storeNo + "'"+item.getValue()+ "'" ;
						}
						else {
							storeNo = storeNo + Constants.DELIMETER_COMMA + "'"+item.getValue()+ "'" ;
							
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
			 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				 cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
			 }
			 Long programTierId = null;
				if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					programTierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
				}
			 boolean isTransacted = false;
			 if(transCardsChkId.isChecked()) {
				 isTransacted = true;
			 }
			String employeeIdStr = Constants.STRING_NILL;
			 for (Listitem item : employeeLbId.getSelectedItems()) 
		        {
				  if(item.getValue() != null && !item.getValue().toString().equalsIgnoreCase("All")) {
					if(employeeIdStr.length() == 0) {
						employeeIdStr = employeeIdStr + "'"+item.getValue()+ "'" ;
						}
						else {
							employeeIdStr = employeeIdStr + Constants.DELIMETER_COMMA +  "'"+item.getValue()+ "'" ;
							}
					}
				 }

			String filePath = exportDir +  "Loyalty_Tiers Memberships_Report_" + prgmName + "_" +
					MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			                try
			                {
							filePath = filePath + "_Tiers Memberships.csv";
							logger.debug("Download File path : " + filePath);
							File file = new File(filePath);
							bw = new BufferedWriter(new FileWriter(filePath));
						    sb =  new StringBuffer();
							sb.append("\""+"Tiers Memberships"+"\"\n");
							if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
							sb.append("\""+"Tier Name (Tier Level)"+"\","+"\""+"Total Memberships"+"\"\n");}
							else{
								sb.append("\""+"Tier Name (Tier Level)"+"\","+"\""+"Total Memberships"+"\"\n");	
							}
							long totMemberships = 0;
							/*Map<Long, Long> enrolledCount ;
							List<Object[]> upgradedCount ;
							long totCount = 0, totCount1 = 0;
						    
						    LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
						    List<Object[]> linktierList = null;
							String linkCardSetStr = "";
							Long cardsetIdLink = null;
							 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
								 cardsetIdLink = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
							 }
							 Long tierIdLink = null;
							 if(!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
								 tierIdLink = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
							 }
							 
							linktierList = loyaltyCardSetDao.fetchCardSetAndLinkedTierByPrgmId(prgmId, cardsetIdLink,tierIdLink);
							
							String linkTierStr = "";
							if(linktierList !=null && linktierList.size()>0){
								for(Object[] obj:linktierList){
									if(linkCardSetStr.length() == 0) {
										linkCardSetStr = linkCardSetStr + obj[0].toString() ;
										linkTierStr = linkTierStr + obj[1].toString();
										}
										else {
											linkCardSetStr = linkCardSetStr + Constants.DELIMETER_COMMA + obj[0].toString();
											linkTierStr = linkTierStr + Constants.DELIMETER_COMMA + obj[1];
										}
								}
								enrolledCount = ltyPrgmSevice.getTierEnrolledCount(userId,prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),linkCardSetStr,linkTierStr,isTransacted,storeNo,employeeIdStr);
								}else{
									enrolledCount = new HashMap<Long, Long>(0);
								}*/
					        
						    LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
							List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
								
								//tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDateStr,endDateStr,tierStr);
								
									/*if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									sb.append("\"");sb.append(enrolledCount.get(tier.getTierId())!= null? enrolledCount.get(tier.getTierId())+"":0+"");sb.append("\",");
									totCount = totCount +( enrolledCount.get(tier.getTierId()) != null? enrolledCount.get(tier.getTierId()):0);
									}
									if(programTierId != null)
									{
									upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),programTierId,isTransacted,storeNo,cardsetId,employeeIdStr);
									}
									else{
									upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),tier.getTierId(),isTransacted,storeNo,cardsetId,employeeIdStr);
									}
									if(upgradedCount != null && upgradedCount.size()>0) {
										for(Object[] obj : upgradedCount) {
											sb.append("\"");sb.append(obj[0]==null? 0+"" :obj[0]+"");sb.append("\",");
										    totCount1 = totCount1 + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));
										}}else{
											sb.append("\"");sb.append(0+"");sb.append("\",");
										}*/
							    if(listOfTiers != null && listOfTiers.size() > 0){
								for(LoyaltyProgramTier tier : listOfTiers){
								List<Object[]> tierobjArr = null;
									if(programTierId != null){
										//tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),programTierId,isTransacted,storeNo,cardsetId,employeeIdStr);
										tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),programTierId,isTransacted,subsidiaryNo,storeNo,cardsetId,employeeIdStr);
										LoyaltyProgramTier ltyTier = loyaltyProgramTierDao.getTierById(programTierId);
										sb.append("\"");sb.append(ltyTier.getTierName()+" "+"("+ltyTier.getTierType()+")");sb.append("\",");
                                        }else{
        									//tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),tier.getTierId(),isTransacted,storeNo,cardsetId,employeeIdStr);
        									tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null),tier.getTierId(),isTransacted,subsidiaryNo,storeNo,cardsetId,employeeIdStr);
									sb.append("\"");sb.append(tier.getTierName()+" "+"("+tier.getTierType()+")");sb.append("\",");
                                        }
									if(tierobjArr != null && tierobjArr.size()>0) {
										for(Object[] obj : tierobjArr) {
											
											sb.append("\"");sb.append(obj[0]==null? 0+"" :obj[0]+"");sb.append("\"\n");
											totMemberships = totMemberships + (obj[0] == null ? 0 : Long.parseLong(obj[0].toString()));

										}}else{
											sb.append("\"");sb.append(0+"");sb.append("\"\n");
											}
									if(programTierId != null) break;
											}
									
										bw.write(sb.toString());
										sb =  new StringBuffer();
										}
								if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
							    sb.append("\""+"TOTAL"+"\","+"\""+totMemberships+"\"\n");}else{
								sb.append("\""+"TOTAL"+"\","+"\""+totMemberships+"\"\n");
							}
							bw.write(sb.toString());
							
		
							bw.flush();
							bw.close();
							Filedownload.save(file, "text/csv");
							
					}catch(Exception e){
						logger.error("Exception :: ",e);
					}finally{
						sb = null;
						bw = null;
						userName = null;
						usersParentDirectory = null;
						exportDir = null ;
						downloadDir = null;

					}
		}}
	
	
	/*private void redrawTierSummary(Long prgmId,String startDateStr,
			String endDateStr,boolean isTransacted,String storeNo,Long cardsetId,String employeeIdStr,Long programTierId){*/
	private void redrawTierSummary(Long prgmId,String startDateStr,
		String endDateStr,boolean isTransacted, String subsidiaryNo, String storeNo,Long cardsetId,String employeeIdStr,Long programTierId){
		resetBreakTierCols();
		Components.removeAllChildren(tierbreakRowsId);
		Components.removeAllChildren(tierbreakFooterId);
		DecimalFormat f = new DecimalFormat("#0.00");
		long totMemberships = 0;
		//Map<Long, Long> enrolledCount ;
		//List<Object[]> upgradedCount ;
		//long totCount = 0, totCount1 = 0;
	    try
	    {
	    /*LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
	    List<Object[]> linktierList = null;
		String linkCardSetStr = "";
		Long cardsetIdLink = null;
		 if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			 cardsetIdLink = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
		 }
		 Long tierIdLink = null;
		 if(!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			 tierIdLink = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
		 }
		 
		linktierList = loyaltyCardSetDao.fetchCardSetAndLinkedTierByPrgmId(prgmId, cardsetIdLink,tierIdLink);
		String linkTierStr = "";
		if(linktierList !=null && linktierList.size()>0){
			for(Object[] obj:linktierList){
				if(linkCardSetStr.length() == 0) {
					linkCardSetStr = linkCardSetStr + obj[0].toString() ;
					}
					else {
						linkCardSetStr = linkCardSetStr + Constants.DELIMETER_COMMA + obj[0].toString();
					}
				if(linkTierStr.length() == 0){
					linkTierStr = linkTierStr + obj[1].toString();
				}else{
					linkTierStr = linkTierStr + Constants.DELIMETER_COMMA + obj[1];
				}
			}
			enrolledCount = ltyPrgmSevice.getTierEnrolledCount(userId,prgmId,startDateStr,endDateStr,linkCardSetStr,linkTierStr,isTransacted,storeNo,employeeIdStr);
			logger.info("ENROLL"+enrolledCount);
			}else{
				enrolledCount = new HashMap<Long, Long>(0);
			}*/
        
	    LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
		
			
			
				/*if(tierIdLink != null){
				LoyaltyProgramTier ltyTier = loyaltyProgramTierDao.getTierById(tierIdLink);
			    row.appendChild(new Label(ltyTier.getTierName()+" "+"("+ltyTier.getTierType()+")"));
				if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				row.appendChild(new Label(enrolledCount.get(ltyTier.getTierId())!= null? enrolledCount.get(ltyTier.getTierId())+"":0+""));
				totCount = totCount +( enrolledCount.get(ltyTier.getTierId()) != null? enrolledCount.get(ltyTier.getTierId()):0);
				logger.info("TIER1"+ltyTier.getTierId());
				logger.info("TOTT"+totCount);
				}
				}else{
					row.appendChild(new Label(tier.getTierName()+" "+"("+tier.getTierType()+")"));
					if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
					row.appendChild(new Label(enrolledCount.get(tier.getTierId())!= null? enrolledCount.get(tier.getTierId())+"":0+""));
					totCount = totCount +( enrolledCount.get(tier.getTierId()) != null? enrolledCount.get(tier.getTierId()):0);
					logger.info("TOTTAA"+totCount);
					
				}
				}
				if(programTierId != null){
					upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,startDateStr,endDateStr,programTierId,isTransacted,storeNo,cardsetId,employeeIdStr);
					
				}else{
			    upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,startDateStr,endDateStr,tier.getTierId(),isTransacted,storeNo,cardsetId,employeeIdStr);
				}
				if(upgradedCount != null && upgradedCount.size()>0) {
					for(Object[] obj : upgradedCount) {
					    row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
					    totCount1 = totCount1 + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));
					}}else{
						row.appendChild(new Label(0+""));
					}*/
		//tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDateStr,endDateStr,tierStr);
		     if(listOfTiers != null && listOfTiers.size() > 0){
		    	 List<Object[]> tierobjArr = ltyPrgmSevice.getTotalMemberships(userId, prgmId,startDateStr,endDateStr,programTierId,isTransacted,subsidiaryNo,storeNo,cardsetId,employeeIdStr);
			  for(LoyaltyProgramTier tier : listOfTiers){
			if(tierobjArr != null && !tierobjArr.isEmpty()){
				for (Object[] objects : tierobjArr) {
					if(objects[1] != null && objects[1].equals(tier.getTierId())){
						Row row = new Row();
						row.appendChild(new Label(tier.getTierName()+" "+"("+tier.getTierType()+")"));
						row.appendChild(new Label(objects[0]==null? 0+"" :objects[0]+""));
						totMemberships = totMemberships + (objects[0] == null ? 0 : Long.parseLong(objects[0].toString()));
						row.setParent(tierbreakRowsId);

					}
					
				}
				
			}
			   /*Row row = null;
				if(programTierId != null){
					//tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,startDateStr,endDateStr,programTierId,isTransacted,storeNo,cardsetId,employeeIdStr);
					LoyaltyProgramTier ltyTier = loyaltyProgramTierDao.getTierById(programTierId);
					row.appendChild(new Label(ltyTier.getTierName()+" "+"("+ltyTier.getTierType()+")"));
					
				}else
				{
					//tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,startDateStr,endDateStr,tier.getTierId(),isTransacted,storeNo,cardsetId,employeeIdStr);
					tierobjArr =ltyPrgmSevice.getTotalMemberships(userId, prgmId,startDateStr,endDateStr,tier.getTierId(),isTransacted,subsidiaryNo,storeNo,cardsetId,employeeIdStr);
				row.appendChild(new Label(tier.getTierName()+" "+"("+tier.getTierType()+")"));
				}
				if(tierobjArr != null && tierobjArr.size()>0) {
					for(Object[] obj : tierobjArr) {
						row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
						totMemberships = totMemberships + (obj[0] == null ? 0 : Long.parseLong(obj[0].toString()));

					}}else{
						row.appendChild(new Label(0+""));
						}*/
				
					if(programTierId != null) break;

				}
					Footer footer = new Footer();
					footer.appendChild(new Label("TOTAL"));
					footer.setParent(tierbreakFooterId);
					
					
					footer = new Footer();
					footer.appendChild(new Label(totMemberships+""));
					footer.setParent(tierbreakFooterId);
					
				}	}
				catch(Exception e){
					logger.error("Exception ::",e);
				}
				}
}
