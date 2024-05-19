package org.mq.optculture.controller.loyalty;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Flashchart;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;

public class LtyProgramOverviewReportController  extends GenericForwardComposer {
	
	private LoyaltyProgramService ltyPrgmSevice;
	private Long userId;
	private Long prgmId;
	private LoyaltyProgram prgmObj;
	private TimeZone clientTimeZone;
	private Label listLblId,performanceLblId;
	private Rows transRowsId,liabilityRowsId,storeRowsId,tierRowsId;
	private Foot transFooterId,liabilityFooterId,storeFooterId,tierFooterId;
	private Radio revenueRadioId,visitsRadioId;
	private Div pieLabelDivId,pieChartDivId,tierDivId;
	private Radiogroup performanceRadioGrId;
	private Chart performanceChartId,activeStoresChartId;
	private Flashchart optinMediumChartId;
	private Calendar startDate,endDate;
	private Columns liabilityColsId,tierColsId;
	private static final String DATE_DIFF_TYPE_DAYS = "days";
	private Columns storeColsId;
	//private Column giftCardIssId;
	private Listheader giftCardIssId;
	private Grid storeLbId,tierSummaryLbId;
	private Listbox storeListLBId;
	private Label enrollFooterId, issFooterId;
	private List<OrganizationStores> storeList;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listfoot storeFooterDivID;
	private final String FOOTER="footer";
	
	public LtyProgramOverviewReportController() {
		ltyPrgmSevice = new LoyaltyProgramService();
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		prgmId = (Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
	}

	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		if(prgmObj.getTierEnableFlag() == 'Y'){
			tierDivId.setVisible(true);
		}else{
			tierDivId.setVisible(false);
		}
		storeList = ltyPrgmSevice.getAllStores(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		getDateValues();
		performanceChartId.setEngine(new LineChartEngine());
		performanceLblId.setValue("("+MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+
				MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")");
		revenueRadioId.setSelected(true);
		setPlotData();
		

		listLblId.setValue("("+MyCalendar.calendarToString(new MyCalendar(clientTimeZone), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")");
		
		redrawLiabilityCount(prgmId);
		redrawTransactionsCount(prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		//redrawTierSummary(prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		setPieChartData();
		resetTierCols();
		
		redrawRegStore();
		activeStoresChartId.setEngine(new JFreeChartEngine());
		setStoreActivityData();
		
		 Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			
			objMap.put(0, OrganizationStores.class.getDeclaredField("subsidiaryName"));
			objMap.put(1, OrganizationStores.class.getDeclaredField("storeName"));
			
			storeListLBId.setAttribute(FOOTER, storeFooterDivID);
			LBFilterEventListener.lbFilterSetup(storeListLBId, null, null, null, null,  objMap);
		
	
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
		
		startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 7);
		endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
	}

	private void setPlotData() {
		
		logger.debug(" >> before setPlotData time In millis ::"+System.currentTimeMillis());
		
		logger.debug(" startDateStr :"+MyCalendar.calendarToString(startDate, null)+ " AND endDateStr Str ::"+MyCalendar.calendarToString(endDate, null));
		
		CategoryModel model = new SimpleCategoryModel();
		
		List<Object[]> loyaltyRevenue = ltyPrgmSevice.getLoyaltyRevenue(userId,prgmId,MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null),null,null,DATE_DIFF_TYPE_DAYS,null,null);
		List<Map<String,Object>> totalRevenue = ltyPrgmSevice.getTotalRevenueAndVisitsByUserId(userId,MyCalendar.calendarToString(startDate, null),
				MyCalendar.calendarToString(endDate, null));
		if(performanceRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("revenue")) {
			Map<String, Double> revenueMap = null; 
			Map<String, Double> revenueCalcMap = null; 

			if(loyaltyRevenue != null && loyaltyRevenue.size() > 0) {
				revenueMap = new HashMap<String, Double>();
				revenueCalcMap = new HashMap<String, Double>();
				for (Object[] obj : loyaltyRevenue) {
					if(revenueCalcMap.containsKey(obj[1].toString())){
						Double revenuForDay = Double.parseDouble(obj[0].toString())+revenueCalcMap.get(obj[1].toString());
						revenueCalcMap.put(obj[1].toString(),revenuForDay);
					}else{
						
						revenueCalcMap.put(obj[1].toString(),Double.parseDouble(obj[0].toString()));
					}
					/*double revRate = (Double.parseDouble(obj[0].toString()) / totalRevenue) *100;
					revenueMap.put(obj[1].toString(), revRate);*/
				}
				for (String date : revenueCalcMap.keySet()) {
					double revRate = ((revenueCalcMap.get(date)) / new Double (totalRevenue.get(0).get("amt") != null ? (Double)totalRevenue.get(0).get("amt"):0.0  )) *100;
					revenueMap.put(date, revRate);
					
				}
				
				
			}
			
			
			Calendar tempCal=Calendar.getInstance();
			tempCal.setTimeZone(clientTimeZone);
			tempCal.setTimeInMillis(startDate.getTimeInMillis());
			String currDate = "";
			
			performanceChartId.setYAxis("Revenue");
			
			do {
				currDate = ""+tempCal.get(startDate.DATE);
				if(revenueMap != null) {
					model.setValue("Revenue from Loyalty Customers", currDate, revenueMap.containsKey(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) ? revenueMap.get(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) : 0);
				}
				else {
					model.setValue("Revenue from Loyalty Customers", currDate, 0);
				}
				tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
			}while(tempCal.before(endDate) || tempCal.equals(endDate));
			
		}
		else if(performanceRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("visits")) {
			/*List<Object[]> loyaltyVisits = ltyPrgmSevice.getLoyaltyVisitsByPrgmId(userId,prgmId,MyCalendar.calendarToString(startDate, null)
					,MyCalendar.calendarToString(endDate, null),null,null,DATE_DIFF_TYPE_DAYS,null,null);
			Long totalVisits = ltyPrgmSevice.getTotalVisitsByUserId(userId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
			*/Map<String, Double> visitsMap = null; 
			Map<String, Double> visitsCalcMap = null; 
			if(loyaltyRevenue != null && loyaltyRevenue.size() > 0) {
				visitsMap = new HashMap<String, Double>();
				visitsCalcMap= new HashMap<String, Double>();
				
				try {
				for (Object[] obj : loyaltyRevenue) {
					
				
					if(visitsCalcMap.containsKey(obj[1].toString())){
						Double visitsForDay = Double.parseDouble(obj[2].toString())+visitsCalcMap.get(obj[1].toString());
						visitsCalcMap.put(obj[1].toString(),visitsForDay);
					}else{
						
						visitsCalcMap.put(obj[1].toString(),Double.parseDouble(obj[2].toString()));
					}
					
				}
				 
				for (String date : visitsCalcMap.keySet()) {
					double visitRate = ((visitsCalcMap.get(date)) / new Long (totalRevenue.get(1).get("visits") != null ? (Long)totalRevenue.get(1).get("visits"):0 )) *100;
					visitsCalcMap.put(date, visitRate);
					
					/*double visitRate = (Double.parseDouble(obj[0].toString()) / totalVisits) *100;
					visitsMap.put(obj[1].toString(), visitRate);*/
				}
			
				}catch(Exception e) {
			
					logger.error("Exception caught for visits ::",e);
					
				}}
			
	
			Calendar tempCal=Calendar.getInstance();
			tempCal.setTimeZone(clientTimeZone);
			tempCal.setTimeInMillis(startDate.getTimeInMillis());
			String currDate = "";

			performanceChartId.setYAxis("Visits");
			
			do {
				currDate = ""+tempCal.get(startDate.DATE);
				if(visitsMap != null) {
					model.setValue("Visits of Loyalty Customers", currDate, visitsMap.containsKey(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) ? visitsMap.get(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_YEARTODATE)) : 0);
				}
				else {
					model.setValue("Visits of Loyalty Customers", currDate, 0);
				}
				tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
			}while(tempCal.before(endDate) || tempCal.equals(endDate));
		}
		
		
		
		performanceChartId.setModel(model);
		
		//redraw Tier summery

		resetTierCols();
		DecimalFormat f = new DecimalFormat("#0.00");
		
	    long totVisits = 0;
	    double totRevenue = 0.00;
	    Components.removeAllChildren(tierColsId);
	    try
	    {
	    
	    LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
		
			
			//tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDateStr,endDateStr,tierStr);
			if(listOfTiers != null && listOfTiers.size() > 0 && loyaltyRevenue !=null && !loyaltyRevenue.isEmpty()){
				
				Map<String, Double> TierrevenueMap = new HashMap<String, Double>();
				Map<String, Double> TierVisitseMap  = new HashMap<String, Double>();
				
				//List<Object[]> tierobjArr = null;
				
				for (Object[] obj : loyaltyRevenue) {
					if(TierrevenueMap.containsKey(obj[3].toString())){
						Double revenueForDay = Double.parseDouble(obj[0].toString())+TierrevenueMap.get(obj[3].toString());
						TierrevenueMap.put(obj[3].toString(),revenueForDay);
					}else{
						
						TierrevenueMap.put(obj[3].toString(),Double.parseDouble(obj[0].toString()));
					}
					if(TierVisitseMap.containsKey(obj[3].toString())){
						Double revenueForDay = Double.parseDouble(obj[2].toString())+TierVisitseMap.get(obj[3].toString());
						TierVisitseMap.put(obj[3].toString(),revenueForDay);
					}else{
						
						TierVisitseMap.put(obj[3].toString(),Double.parseDouble(obj[2].toString()));
					}
				}
				
				//tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDate,endDateStr, null);
				for(LoyaltyProgramTier tier : listOfTiers){
				
				Row row = null;
				row = new Row();
				row.appendChild(new Label(tier.getTierName()+" "+"("+tier.getTierType()+")"));
				/*if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				row.appendChild(new Label(enrolledCount.get(tier.getTierId())!= null? enrolledCount.get(tier.getTierId())+"":0+""));
				totCount = totCount +( enrolledCount.get(tier.getTierId()) != null? enrolledCount.get(tier.getTierId()):0);
				}
				upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,startDateStr,endDateStr,tier.getTierId());
				if(upgradedCount != null && upgradedCount.size()>0) {
					for(Object[] obj : upgradedCount) {
					    row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
					    totCount1 = totCount1 + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));
					}}else{
						row.appendChild(new Label(0+""));
					}*/

				/*if(tierobjArr != null && tierobjArr.size()>0) {
					for(Object[] obj : tierobjArr) {
						if(obj[2] == null || ((Long)obj[2]).longValue() != tier.getTierId()) continue;
                        row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
						row.appendChild(new Label(obj[1]==null? f.format(0.00):f.format(obj[1])));
						totVisits = totVisits + (obj[0] == null ? 0 : Long.parseLong(obj[0].toString()));
						totRevenue = totRevenue + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1].toString()));

					}
					}else{
						row.appendChild(new Label(0+""));
						row.appendChild(new Label(f.format(0.00)+""));
						
					}
				*/
				if(TierVisitseMap.get(tier.getTierId().toString()) != null){
					row.appendChild(new Label(TierVisitseMap.get(tier.getTierId().toString()).longValue()+""));
					totVisits = totVisits +TierVisitseMap.get(tier.getTierId().toString()).longValue();
				}else{
					row.appendChild(new Label(0+""));
				}
				if(TierrevenueMap.get(tier.getTierId().toString()) != null){
					row.appendChild(new Label(f.format(TierrevenueMap.get(tier.getTierId().toString()))));
					totRevenue = totRevenue +TierrevenueMap.get(tier.getTierId().toString()+"");
				}else{
					row.appendChild(new Label(f.format(0.00)));
				}
				
				
					row.setParent(tierRowsId);
				}

					Footer footer = new Footer();
					footer.appendChild(new Label("TOTAL"));
					footer.setParent(tierFooterId);
					
					
					footer = new Footer();
					footer.appendChild(new Label(totVisits+""));
					footer.setParent(tierFooterId);
					
					footer = new Footer();
					footer.appendChild(new Label(f.format(totRevenue)));
					footer.setParent(tierFooterId);
				}	}
				catch(Exception e){
					logger.error("Exception ::",e);
				}
				

		
	}

	public void onCheck$performanceRadioGrId() {
		setPlotData();
	}
	
	private void setPieChartData() {
		

		logger.debug(">>>>>>>>>>>>> before setPieChartData time in millis ::"+System.currentTimeMillis());
		
		List<Object[]> storeCount = ltyPrgmSevice.getEnrollmentCountByOptinMedium(prgmId,userId,"'"+Constants.CONTACT_LOYALTY_TYPE_POS+"','"+
		Constants.CONTACT_LOYALTY_TYPE_WEBFORM+"','"+Constants.CONTACT_LOYALTY_TYPE_OFFLINE+"'",
				MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		/*int webformCount = ltyPrgmSevice.getEnrollmentCountByOptinMedium(prgmId,userId,Constants.CONTACT_LOYALTY_TYPE_WEBFORM,
				MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		int offlineCount = ltyPrgmSevice.getEnrollmentCountByOptinMedium(prgmId,userId,Constants.CONTACT_LOYALTY_TYPE_OFFLINE,
				MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		*/
		if(storeCount == null || storeCount.isEmpty()){
			pieLabelDivId.setVisible(false);
			pieChartDivId.setVisible(false);
			return;
		}
		PieModel model = new SimplePieModel();
		for (Object[] objects : storeCount) {
			
			if(objects[0] != null && (Long)objects[0] > 0 && objects[1] != null && 
					objects[1].equals(Constants.CONTACT_LOYALTY_TYPE_POS)){
				
				model.setValue("Store", (Long)objects[0]);
			}else if(objects[0] != null && (Long)objects[0] > 0 && objects[1] != null && 
					objects[1].equals(Constants.CONTACT_LOYALTY_TYPE_WEBFORM)){
				
				model.setValue("Web-Form", (Long)objects[0]);
			}else if(objects[0] != null && (Long)objects[0] > 0 && objects[1] != null && 
					objects[1].equals(Constants.CONTACT_LOYALTY_TYPE_OFFLINE)){
				
				model.setValue("Offline", (Long)objects[0]);
			}
			
		}
		optinMediumChartId.setModel(model);

		logger.debug(">>>>>>>>>>>>> after setPieChartData time in millis ::"+System.currentTimeMillis());
	}
	
	private void setStoreActivityData() {
		logger.debug(" >> before setPlotData time In millis ::"+System.currentTimeMillis());

		logger.debug(" startDateStr :"+MyCalendar.calendarToString(startDate, null)+ " AND endDateStr Str ::"+MyCalendar.calendarToString(endDate, null));

		CategoryModel model = new SimpleCategoryModel();

		long totalVisits = ltyPrgmSevice.getAllTransactionsCountByPrgmId(userId,prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null));
		List<Object[]> storeVisits = ltyPrgmSevice.getTransactionCountByStores(userId,prgmId,MyCalendar.calendarToString(startDate, null),MyCalendar.calendarToString(endDate, null)); 
		Map<String, Double> storesMap = null; 
		if(storeVisits != null && storeVisits.size() > 0) {
			storesMap = new HashMap<String,Double>();
			for(int i=0; i<5; i++) {
				if(storeVisits.size() > i && storeVisits.get(i) != null){
					if(storeVisits.get(i)[0] != null && storeVisits.get(i)[0] != null){
						double storeVisitRate = (Double.parseDouble(storeVisits.get(i)[1].toString()) / totalVisits) *100;
						storesMap.put(storeVisits.get(i)[0].toString(), storeVisitRate);
					}
				}
			}
		}

		if(storesMap != null) {
			Set<String> keySet = storesMap.keySet();
			for (String key : keySet) {
				model.setValue("Store Activity", key, storesMap.get(key));
			}
		}
		else {
			model.setValue("Store Activity", "", 0);
		}

		activeStoresChartId.setModel(model);

	}

	
	private void redrawLiabilityCount(Long prgmId) {
		resetGridCols();
		Components.removeAllChildren(liabilityRowsId);
		Components.removeAllChildren(liabilityFooterId);
		long totCount = 0;
		double totAmount = 0.00;
		long totPoints = 0;
		DecimalFormat f = new DecimalFormat("#0.00");
		
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			Row row = null;

			row = new Row();
			Object[] obj = ltyPrgmSevice.getLiabilityData(userId, prgmId,null,null,null);

			row.appendChild(new Label("Active"));
			row.appendChild(new Label(obj[0]==null?0+"":obj[0]+""));
			totCount = totCount + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));

			row.appendChild(new Label(obj[1]==null?f.format(0.00):obj[1]+""));
			totAmount = totAmount + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1]+""));

			row.appendChild(new Label(obj[2]==null?0+"":obj[2]+""));
			totPoints = totPoints + (obj[2] == null ? 0 : Long.parseLong(obj[2]+""));

			row.setParent(liabilityRowsId);

			row = new Row();
			long inventoryCount = ltyPrgmSevice.getInventoryCardsCount(prgmId,null);
			row.appendChild(new Label("Inventory"));
			row.appendChild(new Label(inventoryCount+""));
			totCount = totCount + inventoryCount;
			//Liability Amount
			row.appendChild(new Label("--"));
			//Liability Points
			row.appendChild(new Label("--"));

			row.setParent(liabilityRowsId);
		}
		else {
			Row row = null;

			row = new Row();
			Object[] obj = ltyPrgmSevice.getLiabilityData(userId, prgmId,null,null,null);

			row.appendChild(new Label("Mobile"));
			row.appendChild(new Label(obj[0]==null?0+"":obj[0]+""));
			totCount = totCount + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));

			row.appendChild(new Label(obj[1]==null?f.format(0.00):obj[1]+""));
			totAmount = totAmount + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1]+""));

			row.appendChild(new Label(obj[2]==null?0+"":obj[2]+""));
			totPoints = totPoints + (obj[2] == null ? 0 : Long.parseLong(obj[2]+""));

			row.setParent(liabilityRowsId);

			row.setParent(liabilityRowsId);
		}
		
		Footer footer = new Footer();
		footer.appendChild(new Label("TOTAL"));
		footer.setParent(liabilityFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(totCount+""));
		footer.setParent(liabilityFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(f.format(totAmount)));
		footer.setParent(liabilityFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(totPoints+""));
		footer.setParent(liabilityFooterId);
		
	}

	private void resetGridCols() {
		Components.removeAllChildren(liabilityColsId);

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) || 
				prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
			Column memType = new Column("Membership Type");
			memType.setWidth("40%");
			memType.setParent(liabilityColsId);
			Column enrollCount = new Column("No.of Enrollments");
			enrollCount.setWidth("40%");
			enrollCount.setParent(liabilityColsId);
			Column amount = new Column("Currency");
			amount.setWidth("30%");
			amount.setParent(liabilityColsId);
			Column points = new Column("Points");
			points.setWidth("30%");
			points.setParent(liabilityColsId);

		}
		else {
			Column memType = new Column("Card Status");
			memType.setWidth("35%");
			memType.setParent(liabilityColsId);
			Column enrollCount = new Column("Number of Cards");
			enrollCount.setWidth("45%");
			enrollCount.setParent(liabilityColsId);
			Column amount = new Column("Currency");
			amount.setWidth("30%");
			amount.setParent(liabilityColsId);
			Column points = new Column("Points");
			points.setWidth("30%");
			points.setParent(liabilityColsId);

		}


	}
	private void resetTierCols() {
		Components.removeAllChildren(tierColsId);

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			Column tierName = new Column("Tier Name (Tier Level)");
			tierName.setWidth("40%");
			tierName.setParent(tierColsId);
			/*Column upgradedCount = new Column("Upgraded");
			upgradedCount.setWidth("30%");
			upgradedCount.setParent(tierColsId);
			Column visits = new Column("Visits");
			visits.setWidth("30%");
			visits.setParent(tierColsId);*/
			Column visits = new Column("Visits");
			visits.setWidth("30%");
			visits.setParent(tierColsId);
			Column revenue = new Column("Revenue");
			revenue.setWidth("30%");
			revenue.setParent(tierColsId);

		}
		else {
			Column tierName = new Column("Tier Name (Tier Level)");
			tierName.setWidth("40%");
			tierName.setParent(tierColsId);
			/*Column enrolledCount = new Column("Enrolled");
			enrolledCount.setWidth("40%");
			enrolledCount.setParent(tierColsId);
			Column upgradedCount = new Column("Upgraded");
			upgradedCount.setWidth("30%");
			upgradedCount.setParent(tierColsId);*/
			Column visits = new Column("Visits");
			visits.setWidth("30%");
			visits.setParent(tierColsId);
			Column revenue = new Column("Revenue");
			revenue.setWidth("30%");
			revenue.setParent(tierColsId);

		}
	}
	
	private void redrawTierSummary(Long prgmId,String startDateStr,
			String endDateStr){
		resetTierCols();
		DecimalFormat f = new DecimalFormat("#0.00");
		/*Map<Long, Long> enrolledCount ;
		List<Object[]> upgradedCount ;*/
		//long totCount = 0, totCount1 = 0;
	    long totVisits = 0;
	    double totRevenue = 0.00;
	    Components.removeAllChildren(tierColsId);
	    try
	    {
	    /*LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		List<Object[]> linktierList = loyaltyCardSetDao.fetchCardSetAndLinkedTierByPrgmId(prgmId);
		String linkCardSetStr = "";
		String linkTierStr = "";
		if(linktierList !=null && linktierList.size()>0){
			for(Object[] obj:linktierList){
				if(linkCardSetStr.length() == 0) {
					linkCardSetStr = linkCardSetStr + obj[0].toString();
					}
					else {
						linkCardSetStr = linkCardSetStr + Constants.DELIMETER_COMMA + obj[0];
						
					}
				if(linkTierStr.length() == 0) {
					linkTierStr = linkTierStr + obj[1].toString();
					}
					else {
						linkTierStr = linkTierStr + Constants.DELIMETER_COMMA + obj[1];
						
					}
			}
			enrolledCount = ltyPrgmSevice.getTierEnrolledCount(userId,prgmId,startDateStr,endDateStr,linkCardSetStr,linkTierStr);
			}else{
				enrolledCount = new HashMap<Long, Long>(0);
			}*/
        
	    LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersByProgramId(prgmId);
		
			
			//tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDateStr,endDateStr,tierStr);
			if(listOfTiers != null && listOfTiers.size() > 0){
				List<Object[]> tierobjArr = null;
				
				tierobjArr =ltyPrgmSevice.getTierVisitsAndRevenue(userId, prgmId,startDateStr,endDateStr, null);
				for(LoyaltyProgramTier tier : listOfTiers){
				
				Row row = null;
				row = new Row();
				row.appendChild(new Label(tier.getTierName()+" "+"("+tier.getTierType()+")"));
				/*if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				row.appendChild(new Label(enrolledCount.get(tier.getTierId())!= null? enrolledCount.get(tier.getTierId())+"":0+""));
				totCount = totCount +( enrolledCount.get(tier.getTierId()) != null? enrolledCount.get(tier.getTierId()):0);
				}
				upgradedCount = ltyPrgmSevice.getTierUpgradedCount(userId,prgmId,startDateStr,endDateStr,tier.getTierId());
				if(upgradedCount != null && upgradedCount.size()>0) {
					for(Object[] obj : upgradedCount) {
					    row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
					    totCount1 = totCount1 + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));
					}}else{
						row.appendChild(new Label(0+""));
					}*/

				if(tierobjArr != null && tierobjArr.size()>0) {
					for(Object[] obj : tierobjArr) {
						if(obj[2] == null || ((Long)obj[2]).longValue() != tier.getTierId()) continue;
                        row.appendChild(new Label(obj[0]==null? 0+"" :obj[0]+""));
						row.appendChild(new Label(obj[1]==null? f.format(0.00):f.format(obj[1])));
						totVisits = totVisits + (obj[0] == null ? 0 : Long.parseLong(obj[0].toString()));
						totRevenue = totRevenue + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1].toString()));

					}
					}else{
						row.appendChild(new Label(0+""));
						row.appendChild(new Label(f.format(0.00)+""));
						
					}
				
					row.setParent(tierRowsId);
				}

					Footer footer = new Footer();
					footer.appendChild(new Label("TOTAL"));
					footer.setParent(tierFooterId);
					
					
					footer = new Footer();
					footer.appendChild(new Label(totVisits+""));
					footer.setParent(tierFooterId);
					
					footer = new Footer();
					footer.appendChild(new Label(f.format(totRevenue)));
					footer.setParent(tierFooterId);
				}	}
				catch(Exception e){
					logger.error("Exception ::",e);
				}
				}
				
				
				/*row.appendChild(new Label(enrCount+""));
				row.appendChild(new Label("--"));
				row.appendChild(new Label("--"));
				row.setParent(transRowsId);
		
	   
		
		
		
		
		/*List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(prgmId);
		for (LoyaltyCardSet loyaltyCardSet : list) {
			if(loyaltyCardSet.getLinkedTierLevel() != 0){
				LoyaltyProgram prgmObj = getProgmObj(prgmId);
				List<LoyaltyProgramTier> tierList = getTierList(prgmId);
			}*/
			
				
				
			
			
			

			
			/*Row row = new Row();
			row.appendChild(new Label(loyaltyCardSet.getCardSetName()));
			row.appendChild(new Label(loyaltyCardSet.getQuantity().toString()));
			row.appendChild(new Label(loyaltyCardSet.getCardSetType().toString()));
			row.appendChild(new Label(loyaltyCardSet.getGenerationType()));
	    	
	    	
	    }
		
	    Row row = null;
	    
	    //int enrolledCount = ltyPrgmSevice.getEnrolledCount(prgmId,startDateStr,endDateStr,null,null);
		row = new Row();
		row.appendChild(new Label());
		}*/
	
	    
	
	private void redrawTransactionsCount(Long prgmId, String startDateStr,
			String endDateStr) {
		DecimalFormat f = new DecimalFormat("#0.00");
		int totCount = 0;
		double totAmount = 0;
		Double totPoints = 0.00;
		
		Row row = null;
		Object[] retArr = ltyPrgmSevice.getTrxSummery(userId, prgmId,startDateStr,endDateStr,null,null,null,null,null);
		int enrCount = retArr != null && retArr.length>0 && retArr[0]!= null? (int)retArr[0] :0;//ltyPrgmSevice.getEnrollementTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
		row = new Row();
		row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT));
		totCount = totCount + (enrCount);
		row.appendChild(new Label(enrCount+""));
		row.appendChild(new Label("--"));
		row.appendChild(new Label("--"));
		row.setParent(transRowsId);
		
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
		
		if(!prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			//issObj = ltyPrgmSevice.getIssuanceTrans(prgmId,startDateStr,endDateStr, null, null, null, "giftIssuance",null,null);
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
		int redCount = retArr != null && retArr.length>0 && retArr[6]!= null? (int)retArr[6] :0;
		double redeemAmt = retArr != null && retArr.length>0 && retArr[7]!= null? (double)retArr[7] :0;
		Long redeemPts = 	retArr != null && retArr.length>0 && retArr[8]!= null? (long)retArr[8] :0;
		
		//Object[] redObjAmt = ltyPrgmSevice.getRedemptionTransAmt(prgmId,startDateStr,endDateStr, null, null, null,null,null);
		//Object[] redObjPts = ltyPrgmSevice.getRedemptionTransPts(prgmId,startDateStr,endDateStr, null, null, null,null,null);
		//int redCount = (redObjAmt[0] == null ? 0 : Integer.parseInt(redObjAmt[0]+"")) + (redObjPts[0] == null ? 0 : Integer.parseInt(redObjPts[0]+""));
		row = new Row();
		row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION));
		totCount = totCount + redCount;
		row.appendChild(new Label(redCount+""));
		totAmount = totAmount + redeemAmt ;//(redObjAmt[1] == null ? 0.00 : Double.parseDouble(redObjAmt[1]+""));
		row.appendChild(new Label(redeemAmt== 0 ? f.format(0.00) : f.format(redeemAmt)));
		totPoints = totPoints + redeemPts;
		row.appendChild(new Label(redeemPts +""));
		row.setParent(transRowsId);

		int inqCount = retArr != null && retArr.length>0 && retArr[9]!= null? (int)retArr[9] :0;;//ltyPrgmSevice.getInquiryTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
		row = new Row();
		row.appendChild(new Label(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY));
		totCount = totCount + (inqCount);
		row.appendChild(new Label(inqCount+""));
		row.appendChild(new Label("--"));
		row.appendChild(new Label("--"));
		row.setParent(transRowsId);
		
		/*Object[] reversalObj = ltyPrgmSevice.getReversalTrans(prgmId,startDateStr,endDateStr,  null, null,null,null);
		row = new Row();
		row.appendChild(new Label("Returns"));
		totCount = totCount + (reversalObj[0] == null ? 0 : Integer.parseInt(reversalObj[0]+""));
		row.appendChild(new Label(reversalObj[0] == null ? 0+"" : reversalObj[0]+""));
		//totAmount = totAmount + (reversalObj[1] == null ? 0.00 : Double.parseDouble(reversalObj[1]+""));
		//row.appendChild(new Label(reversalObj[1] == null ? f.format(0.00) : f.format(new Double(reversalObj[1]+""))));
		//totPoints = totPoints + (reversalObj[2] == null ? 0 : Double.parseDouble(reversalObj[2]+""));
		//row.appendChild(new Label(reversalObj[2] == null ? 0+"" : new Double(reversalObj[2]+"").intValue()+""));
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
		
		//Object[] storeCreditObj = ltyPrgmSevice.getStoreCreditTrans(prgmId,startDateStr,endDateStr, null, null, null,null,null);
		row = new Row();
		row.appendChild(new Label("Store Credit"));
		totCount = totCount + ( 0 );
		row.appendChild(new Label(0+""));
		totAmount = totAmount + (0);
		row.appendChild(new Label("0"));
		row.appendChild(new Label("--"));
		row.setParent(transRowsId);
		
		int transferCount = retArr != null && retArr.length>0 && retArr[16]!= null? (int)retArr[16] :0;//ltyPrgmSevice.getTransferTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
		row = new Row();
		row.appendChild(new Label("Transfers"));
		totCount = totCount + (transferCount);
		row.appendChild(new Label(transferCount+""));
		row.appendChild(new Label("--"));
		row.appendChild(new Label("--"));
		row.setParent(transRowsId);
		
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
		
		int adjCount = retArr != null && retArr.length>0 && retArr[20]!= null? (int)retArr[20] :0;
		double adjAmt = retArr != null && retArr.length>0 && retArr[21]!= null? (double)retArr[21] :0;
		Long adjPts = 	retArr != null && retArr.length>0 && retArr[22]!= null? (long)retArr[22] :0;
		
		//Object[] adjustObj = ltyPrgmSevice.getAdjustmentTrans(prgmId,startDateStr, endDateStr, null, null, null,null,null);
		row = new Row();
		row.appendChild(new Label("Adjustments"));
		totCount = totCount+ (adjCount);
		row.appendChild(new Label(adjCount+ ""));
		totAmount = totAmount+adjAmt;// (adjustObj[1] == null ? 0.00 : Double.parseDouble(adjustObj[1]+ ""));
		row.appendChild(new Label(adjAmt == 0? f.format(0.00) : f.format(adjAmt)));
		totPoints = totPoints+ (adjPts);
		row.appendChild(new Label(adjPts + ""));
		row.setParent(transRowsId);
		
		
		int changeTierCount = retArr != null && retArr.length>0 && retArr[23]!= null? (int)retArr[23] :0;//ltyPrgmSevice.getChangeTierTrans(prgmId,startDateStr,endDateStr,null,null,null,null,null);
		row = new Row();
		row.appendChild(new Label("Tier Adjustment"));
		totCount = totCount + (changeTierCount);
		row.appendChild(new Label(changeTierCount+""));
		row.appendChild(new Label("--"));
		row.appendChild(new Label("--"));
		row.setParent(transRowsId);
		
		Footer footer = new Footer();
		footer.appendChild(new Label("TOTAL"));
		footer.setParent(transFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(totCount+""));
		footer.setParent(transFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(f.format(totAmount)));
		footer.setParent(transFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(totPoints.intValue()+""));
		footer.setParent(transFooterId);
		
	}
	private void resetStoreGridCols() {
		

		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(prgmObj.getMembershipType())) {
			giftCardIssId.setVisible(false);
		}

	}

	/*private void redrawRegStore() {

		resetStoreGridCols();

		Components.removeAllChildren(storeRowsId);
		Components.removeAllChildren(storeFooterId);
		int totReg = 0;
		int totGift = 0;

		//Mobile Based Program

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			Row row = null;

			List<Object[]> objArr = null;
			objArr =ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),true,null);
			if(objArr != null && objArr.size() > 5) {
				storeLbId.setHeight("240px");
			}
			if(objArr != null) {
				for(Object[] obj : objArr) {
					row = new Row();
                    if(obj[0] != null){
					String sName = "Store ID "+obj[0].toString() ;
					String sbsName = "Subsidiary ID "+obj[2].toString() ;
					for (OrganizationStores org : storeList){
					if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
				    	sName = org.getStoreName().toString();
				    	break;
						if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
								obj[2].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()){
					    	sName = org.getStoreName().toString();
					    	sbsName = org.getSubsidiaryName().toString();
					    	break;						
					}}
					row.appendChild(new Label(sbsName));
					row.appendChild(new Label(sName));
					}else{
						row.appendChild(new Label(""));
						}
					row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
					totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));

					row.setParent(storeRowsId);
				}

				Footer footer = new Footer();
				footer.appendChild(new Label("TOTAL"));
				footer.setParent(storeFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totReg+""));
				footer.setParent(storeFooterId);
			}
		}else {
			Row row = null;
			String storeNo;
			String sbsNo;

			List<Object[]> loyaltyObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),false,"loyalty");
			List<Object[]> giftObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),false,"gift");

			List <Object[]> objArr = null;
			if(loyaltyObjArr != null && loyaltyObjArr.size() > 0) {
				objArr = new ArrayList<Object[]>();
				for(Object[] obj : loyaltyObjArr) {
					
					//Object[] storeObj = new Object[3];
					Object[] storeObj = new Object[4];
					storeObj[0] = obj[0];
					storeObj[1] = obj[1] == null ? 0+"" :obj[1]+"";
					storeObj[3] = obj[2];
					objArr.add(storeObj);
				}

			}
			if(giftObjArr != null && giftObjArr.size() > 0) {

				if(objArr == null) {
					objArr = new ArrayList<Object[]>();
					for(Object[] gftObj : giftObjArr) {
						//Object[] storeObj = new Object[3];
						Object[] storeObj = new Object[4];
						storeObj[0] = gftObj[0];
						storeObj[3] = gftObj[2];
						storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
						objArr.add(storeObj);
					}
				}
				else {
					for(Object[] gftObj : giftObjArr) {
						boolean isExists = false;
						for (Object[] existObj : objArr) {
							logger.info("existObj[0]"+existObj[0] +"existObj[1]-------------"+existObj[1]+"existObj[2]"+existObj[2]);
							//if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString())) {
							if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString()) && existObj[3].toString().equalsIgnoreCase(gftObj[2].toString())) {
								existObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
								isExists = true;
								break;
							}
						}
						if(!isExists) {
							Object[] storeObj = new Object[3];
							storeObj[0] = gftObj[0];
							storeObj[3] = gftObj[2];
							storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
							objArr.add(storeObj);
						}
					}
				}
			}
			if(objArr != null && objArr.size() > 5) {
				storeLbId.setHeight("240px");
			}
			if(objArr != null && objArr.size() > 0) {
				for(Object[] obj : objArr) {
					row = new Row();
					if(obj[0] != null){
						storeNo = "Store ID "+obj[0].toString() ;
						sbsNo = "Subsidiary ID"+obj[3].toString();
						for (OrganizationStores org : storeList){
						//if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
						if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
							    obj[3].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
							storeNo = org.getStoreName().toString();
							sbsNo = org.getSubsidiaryName().toString();
					    	break;
						}}
						}else{
							storeNo = "";
							sbsNo ="";
							}

					row.appendChild(new Label(sbsNo));
					row.appendChild(new Label(""+storeNo));
					row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
					row.appendChild(new Label(obj[2]==null? 0+"" :obj[2]+""));
					totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));
					totGift = totGift + (obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));

					row.setParent(storeRowsId);
				}

				Footer footer = new Footer();
				footer.appendChild(new Label("TOTAL"));
				footer.setParent(storeFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totReg+""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totGift+""));
				footer.setParent(storeFooterId);
			}
		}
	}*/
	private void redrawRegStore() {

		resetStoreGridCols();

		/*Components.removeAllChildren(storeRowsId);
		Components.removeAllChildren(storeFooterId);*/
		int totReg = 0;
		int totGift = 0;
		
		MessageUtil.clearMessage();
		logger.debug("-- just entered --");
		int count =  storeListLBId.getItemCount();
		
		for(; count>0; count--) {
			 storeListLBId.removeItemAt(count-1);
		}

		//Mobile Based Program

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			//Row row = null;
			
			Listitem li = null;
			Listcell lc = null;

			List<Object[]> objArr = null;
			objArr =ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),true,null);
			/*if(objArr != null && objArr.size() > 5) {
				storeLbId.setHeight("240px");
			}*/
			if(objArr != null) {
				for(Object[] obj : objArr) {
					//row = new Row();
					lc = new Listcell();
					li = new Listitem();
                    if(obj[0] != null){
					String sName = "Store ID "+obj[0].toString() ;
					String sbsName = "Subsidiary ID "+obj[2].toString() ;
					for (OrganizationStores org : storeList){
					/*if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
				    	sName = org.getStoreName().toString();
				    	break;*/
						if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
								obj[2].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()){
					    	sName = org.getStoreName().toString();
					    	sbsName = org.getSubsidiaryName().toString();
					    	break;						
					}}
					/*row.appendChild(new Label(sbsName));
					row.appendChild(new Label(sName));*/
					lc.setLabel(sbsName);
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(sName);
					lc.setParent(li);
					}else{
						//row.appendChild(new Label(""));
						lc = new Listcell();
						lc.setLabel("");
						lc.setParent(li);
						
						lc = new Listcell();
						lc.setLabel("");
						lc.setParent(li);
						
						}
					//row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
                    
                    lc = new Listcell();
					lc.setLabel(obj[1]==null? 0+"" :obj[1]+"");
					totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));

					//row.setParent(storeRowsId);
					lc.setParent(li);
					li.setParent( storeListLBId);

				}

				/*Footer footer = new Footer();
				footer.appendChild(new Label("TOTAL"));
				footer.setParent(storeFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totReg+""));
				footer.setParent(storeFooterId);*/
				
				enrollFooterId.setValue(""+totReg);
			}else{
				enrollFooterId.setValue("");
			}
		}else {
			//Row row = null;
			Listitem li = null;
			Listcell lc = null;
			String storeNo;
			String sbsNo;

			List<Object[]> loyaltyObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),false,"loyalty");
			List<Object[]> giftObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString(startDate, null),
					MyCalendar.calendarToString(endDate, null),false,"gift");

			List <Object[]> objArr = null;
			if(loyaltyObjArr != null && loyaltyObjArr.size() > 0) {
				objArr = new ArrayList<Object[]>();
				for(Object[] obj : loyaltyObjArr) {
					
					//Object[] storeObj = new Object[3];
					Object[] storeObj = new Object[4];
					storeObj[0] = obj[0];
					storeObj[1] = obj[1] == null ? 0+"" :obj[1]+"";
					storeObj[3] = obj[2];
					objArr.add(storeObj);
				}

			}
			if(giftObjArr != null && giftObjArr.size() > 0) {

				if(objArr == null) {
					objArr = new ArrayList<Object[]>();
					for(Object[] gftObj : giftObjArr) {
						//Object[] storeObj = new Object[3];
						Object[] storeObj = new Object[4];
						storeObj[0] = gftObj[0];
						storeObj[3] = gftObj[2];
						storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
						objArr.add(storeObj);
					}
				}
				else {
					for(Object[] gftObj : giftObjArr) {
						boolean isExists = false;
						for (Object[] existObj : objArr) {
							logger.info("existObj[0]"+existObj[0] +"existObj[1]-------------"+existObj[1]+"existObj[2]"+existObj[2]);
							//if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString())) {
							if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString()) && existObj[3].toString().equalsIgnoreCase(gftObj[2].toString())) {
								existObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
								isExists = true;
								break;
							}
						}
						if(!isExists) {
							//Object[] storeObj = new Object[3];
							Object[] storeObj = new Object[4];
							storeObj[0] = gftObj[0];
							storeObj[3] = gftObj[2];
							storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
							objArr.add(storeObj);
						}
					}
				}
			}
			/*if(objArr != null && objArr.size() > 5) {
				storeLbId.setHeight("240px");
			}*/
			if(objArr != null && objArr.size() > 0) {
				for(Object[] obj : objArr) {
					//row = new Row();
					lc = new Listcell();
					li = new Listitem();
					if(obj[0] != null){
						storeNo = "Store ID "+obj[0].toString() ;
						sbsNo = "Subsidiary ID"+obj[3].toString();
						for (OrganizationStores org : storeList){
						//if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
						if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
							    obj[3].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
							storeNo = org.getStoreName().toString();
							sbsNo = org.getSubsidiaryName().toString();
					    	break;
						}}
						}else{
							storeNo = "";
							sbsNo ="";
							}

					/*row.appendChild(new Label(sbsNo));
					row.appendChild(new Label(""+storeNo));
					row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
					row.appendChild(new Label(obj[2]==null? 0+"" :obj[2]+""));*/
					lc.setLabel(sbsNo);
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(""+storeNo);
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(obj[1]==null? 0+"" :obj[1]+"");
					lc.setParent(li);
					
					lc = new Listcell();
					lc.setLabel(obj[2]==null? 0+"" :obj[2]+"");
					
					totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));
					totGift = totGift + (obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));

					//row.setParent(storeRowsId);
					lc.setParent(li);
					li.setParent(storeListLBId);

				}

				/*Footer footer = new Footer();
				footer.appendChild(new Label("TOTAL"));
				footer.setParent(storeFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totReg+""));
				footer.setParent(storeFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totGift+""));
				footer.setParent(storeFooterId);*/
				
				enrollFooterId.setValue(""+totReg);
				issFooterId.setValue(""+totGift);

			}else{
				enrollFooterId.setValue("");
				issFooterId.setValue("");
			}
		}
	}
}
