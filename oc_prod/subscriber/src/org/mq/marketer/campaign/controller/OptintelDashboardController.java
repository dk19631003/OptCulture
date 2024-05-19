package org.mq.marketer.campaign.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSFileLogs;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.POSFileLogDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.optculture.utils.ServiceLocator;

public class OptintelDashboardController extends GenericForwardComposer {
	private Radiogroup taxRadioRgId;
	private Radio withtaxRgId,withouttaxRgId;
	private Label totRevenueLblId,noOfPurchaseLblId,revenurFromPromosLblId,totPromoRedemptionsLblId,returningCustLblId,newCustLblId;
	private MyDatebox activityFilterStartDtBxId,activityFilterEndDtBxId;
	private RetailProSalesDao retailProSalesDao;
	private Users user;
	private OrganizationStoresDao organizationStoresDao;
	private MailingListDao mailingListDao;
	private MailingList posMailingList;
	private SkuFileDao skuFileDao;
	private POSFileLogDao posFileLogDao;
	private MyDatebox trendsStartDateId,trendsEndDateId;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // HH:mm:ss
	private DateFormat dateFormat2 = new SimpleDateFormat(MyCalendar.FORMAT_STDATE);
	private Label topSellingItemLblId,topSellingProdLblId,topSellingStoreLblId,hightestRevDayLblId;
//	private A optintelRepImgId,optintelSettingImgId,viewLogsAId;
	private A actOneDayAId,actOneWeekAId,actOneMonthAId,actThreeMonthAId,actSixMonthAId,actOneYrAId;
	private A trendsLstWeekAId,trendsLstMonthAId,trendsLstThreeMnthsAId,trendsLastSixMonthsAId;
	private Div pastDatesRevDivId,noOfPurDivId,revenueFrPromoDivId,totPromoRedtnsDivId,returnCustDivId,newCustDivId,compareToDivId;
	private Label pastDatesRevLblId,noOfPurLblId,pastStartAndEndDateLblId,pastRevFrPromLblId,pastTotPromRedLblId,pastReturnCustLblId,pastNewCustLblId;
	private Label lastFetchedTimeLblId;
	private Checkbox compToPastChkBxId;
	private Window viewLogsWinId;
	private Grid viewLogsWinId$viewLogsGridId;
	private String pastStartCalStr,pastEndCalStr;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	
	private static final String _TRENDS = "trends";
	private static final String _ACTIVITY = "activity";
	
	private static final String DATE_DIFF_TYPE_DAYS = "days";
	private static final String DATE_DIFF_TYPE_MONTHS = "months";
	private static final String DATE_DIFF_TYPE_YEAR = "year";
	
	
	private Double totRevenueVal;
	private int noOfpurchaseVal;
	private Double revenueFromPromosVal;
	private int totPromoRedemptionsVal;
	private int returiningCustVal;
	private int newCustVal;
	
	
	public OptintelDashboardController() {
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		long starttime = System.currentTimeMillis();
		try {
			logger.debug("Loading Optintel Dashboard...");
			user = GetUser.getUserObj();
			retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
			mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			posMailingList = mailingListDao.findPOSMailingList(user);
			skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
			posFileLogDao = (POSFileLogDao)SpringUtil.getBean("posFileLogDao");
			
			if(posMailingList == null) {
				logger.debug("No pos type mailing list exist for the user :"+ user.getUserName());
				return;
			}
			
		//	onClick$actOneWeekAId();
		//	onClick$trendsLstWeekAId();
			/*
			Date endDate = new Date();
			endDate.setDate(endDate.getDate() - 1);
			activityFilterEndDtBxId.setValue(endDate);
			Date date = new Date();
			date.setDate(endDate.getDate() - 6);
			activityFilterStartDtBxId.setValue(date);
			
			trendsEndDateId.setValue(endDate);
			trendsStartDateId.setValue(date);*/
			
			/*setActivityFields(activityFilterStartDtBxId.getValue(),activityFilterEndDtBxId.getValue());
			setTrendsFields(trendsStartDateId.getValue(),trendsEndDateId.getValue());*/
			//setActivityFields(activityFilterStartDtBxId.getServerValue(),activityFilterEndDtBxId.getServerValue());
			//setTrendsFields(trendsStartDateId.getServerValue(),trendsEndDateId.getServerValue());
			// Set last fetch time .
			List<POSFileLogs> poslist = posFileLogDao.findAllByIdAndSize(GetUser.getUserId(),0,1);
			if(poslist != null && poslist.size() > 0) {
				POSFileLogs posFile = poslist.get(0);
				lastFetchedTimeLblId.setValue(MyCalendar.calendarToString(posFile.getFetchedTime(), MyCalendar.FORMAT_DATETIME_STYEAR)+
						"  "+posFile.getFetchedTime().getTimeZone().getDisplayName()+"  ( "+posFile.getFetchedTime().getTimeZone().getID()+" )");
				
			} else {
				lastFetchedTimeLblId.setValue("No logs found");
			}
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}	
		
		long endtime = System.currentTimeMillis() - starttime;
		logger.fatal("**PerfTest** Total Time OptintelDashboardController... "+endtime);
	}
		
	
	private void setActivityFields(Calendar startDate,Calendar endDate) {
		try {
			
			logger.debug("list id : "+ posMailingList.getListId());
			Long userId =user.getUserId();
			String startCalStr = MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_YEARTODATE); 
			String endCalStr = MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_YEARTODATE); 
			
//			String tempStr = startCalStr;
			
			startCalStr = startCalStr+" 00:00:00";
			endCalStr = endCalStr+" 23:59:59";
//			Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(posMailingList.getListId(),dateFormat.format(startDate),dateFormat.format(endDate));
//			Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(posMailingList.getListId(),startCalStr, endCalStr);
			Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(userId,startCalStr, endCalStr, true);
			if(obj != null && obj.length > 0) {
				
			/*	logger.debug("obj lenght" + obj.length);
				totRevenueVal = (Double)obj[0];
				
				NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
				totRevenueLblId.setValue(numberFormat.format(BigDecimal.valueOf((Double)obj[0])));
				
				//totRevenueLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
				logger.debug("Total Revenue :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
				noOfpurchaseVal = (Integer)obj[1];
				
				noOfPurchaseLblId.setValue(numberFormat.format((Integer)obj[1]));*/
				//noOfPurchaseLblId.setValue(obj[1].toString());
			}
			
			// Get promo values ...
			obj = null;
			obj = retailProSalesDao.getPromosTotalAndRevenue(userId,startCalStr, endCalStr, true);
			if(obj != null) {
			//	revenueFromPromosVal = (Double)obj[0];
				
				NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		//		revenurFromPromosLblId.setValue(numberFormat.format((Double)obj[0]));
				
				//revenurFromPromosLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
		//		logger.debug("Revenue From Promos :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
				totPromoRedemptionsVal = (Integer)obj[1];
				totPromoRedemptionsLblId.setValue(numberFormat.format((Integer)obj[1]));
				logger.info("totalpromoredemptions"+(Integer)obj[1]);
				//totPromoRedemptionsLblId.setValue(obj[1].toString());
			}
			
			
			
			// set New and returnig customers
			//Date startDate = activityFilterStartDtBxId.getValue();
			//Date endDate = activityFilterEndDtBxId.getValue();		
			
			obj = null;
//			obj = retailProSalesDao.getNewCustomers(userId,startCalStr, endCalStr);
			newCustVal = retailProSalesDao.getNewCustomers(userId,startCalStr, endCalStr);
			
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			newCustLblId.setValue(numberFormat.format(newCustVal));
			//newCustLblId.setValue(""+newCustVal);
			/*if(obj != null) {
				
				newCustVal = (Integer)obj[0];
				newCustLblId.setValue(obj[0].toString());
			}*/
			
			obj = null;
			//obj = retailProSalesDao.getReturningCustomers(posMailingList.getListId(),dateFormat.format(startDate));
//			tempStr = tempStr+" 23:59:59";
			returiningCustVal = retailProSalesDao.getReturningCustomers(userId,startCalStr,endCalStr);
			numberFormat = NumberFormat.getNumberInstance(Locale.US);
			returningCustLblId.setValue(numberFormat.format(returiningCustVal));
			//returningCustLblId.setValue(""+returiningCustVal);
			/*if(obj != null) {
				
				returiningCustVal = (Integer)obj[0];
				returningCustLblId.setValue(obj[0].toString());
			}*/
			if(withtaxRgId.isSelected()) {
				
				onCheck$withtaxRgId();
			}
			else if(withouttaxRgId.isSelected()) {
				
				onCheck$withouttaxRgId();
			}
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
			
			
			
	public void onCheck$withtaxRgId(){
		try {	
		Calendar start = activityFilterStartDtBxId.getClientValue();
		Calendar end = activityFilterEndDtBxId.getClientValue();
		Long userId =user.getUserId();
		
		String startCalStr = MyCalendar.calendarToString(start, MyCalendar.FORMAT_YEARTODATE); 
		String endCalStr = MyCalendar.calendarToString(end, MyCalendar.FORMAT_YEARTODATE); 

		startCalStr = startCalStr+" 00:00:00";
		endCalStr = endCalStr+" 23:59:59";
		Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(userId,startCalStr, endCalStr, true);
		if(obj != null && obj.length > 0) {
		
	logger.debug("obj lenght" + obj.length);
	totRevenueVal = (Double)obj[0];
	
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
	totRevenueLblId.setValue(numberFormat.format(BigDecimal.valueOf((Double)obj[0])));
	
	//totRevenueLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
	logger.debug("Total Revenue :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
	noOfpurchaseVal = (Integer)obj[1];
	
	noOfPurchaseLblId.setValue(numberFormat.format((Integer)obj[1]));
	logger.info("noOfpurchase:"+ (Integer)obj[1]);
		}
		
	//noOfPurchaseLblId.setValue(obj[1].toString());
	obj = null;
	obj = retailProSalesDao.getPromosTotalAndRevenue(userId,startCalStr, endCalStr, true);
	if(obj != null) {
		revenueFromPromosVal = (Double)obj[0];
		
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		revenurFromPromosLblId.setValue(numberFormat.format((Double)obj[0]));
		
		//revenurFromPromosLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
		logger.debug("Revenue From Promos :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
		}
	}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	public void onCheck$withouttaxRgId() {

		try{
		Calendar start = activityFilterStartDtBxId.getClientValue();
		Calendar end = activityFilterEndDtBxId.getClientValue();
		Long userId =user.getUserId();
		
		String startCalStr = MyCalendar.calendarToString(start, MyCalendar.FORMAT_YEARTODATE); 
		String endCalStr = MyCalendar.calendarToString(end, MyCalendar.FORMAT_YEARTODATE); 
		startCalStr = startCalStr+" 00:00:00";
		endCalStr = endCalStr+" 23:59:59";
	
		Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(userId,startCalStr, endCalStr, false);
		if(obj != null && obj.length > 0) {
		 logger.debug("obj lenght" + obj.length);
			totRevenueVal = (Double)obj[0];
			
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			totRevenueLblId.setValue(numberFormat.format(BigDecimal.valueOf((Double)obj[0])));
			
			//totRevenueLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
			logger.debug("Total Revenue :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
			noOfpurchaseVal = (Integer)obj[1];
			
			noOfPurchaseLblId.setValue(numberFormat.format((Integer)obj[1]));
			//noOfPurchaseLblId.setValue(obj[1].toString());
		}
			obj = null;
			obj = retailProSalesDao.getPromosTotalAndRevenue(userId,startCalStr, endCalStr, false);
			if(obj != null) {
				revenueFromPromosVal = (Double)obj[0];
				
				NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
				revenurFromPromosLblId.setValue(numberFormat.format((Double)obj[0]));
				
				//revenurFromPromosLblId.setValue(BigDecimal.valueOf((Double)obj[0]).toPlainString());
				logger.debug("Revenue From Promos :"+BigDecimal.valueOf((Double)obj[0]).toPlainString());
		}
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		
		}

	}
	
	
	
	

	public void onChange$activityFilterStartDtBxId() {
		//logger.debug("--1--");
		refreshActivityFields();
		
	}
	
	public void onChange$activityFilterEndDtBxId() {
		//logger.debug("--2---");
		refreshActivityFields();
	}
	
	private void refreshActivityFields() {
		
		try {
			
			/*Date start = activityFilterStartDtBxId.getValue();
			Date end = activityFilterEndDtBxId.getValue();*/
			
			Calendar start = activityFilterStartDtBxId.getClientValue();
			Calendar end = activityFilterEndDtBxId.getClientValue();
			
			if(end.before(start)) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				onClick$actOneDayAId();
				return;
			}
			
			totRevenueLblId.setValue("");
			noOfPurchaseLblId.setValue("");
			revenurFromPromosLblId.setValue("");
			totPromoRedemptionsLblId.setValue("");
			newCustLblId.setValue("");
			returningCustLblId.setValue("");
			
			setActivityFields(start,end);
			
			// Check past date field.
			onClick$compToPastChkBxId();
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	
	public void onChange$trendsEndDateId() {
		refreshTrendsFields();
	}
	
	public void onChange$trendsStartDateId() {
		refreshTrendsFields();
	}
	
	private void refreshTrendsFields() {
		try
		{
		/*Date end = trendsEndDateId.getValue();
		Date start = trendsStartDateId.getValue();*/
		Calendar end = trendsEndDateId.getClientValue();
		Calendar start = trendsStartDateId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'Start' date cannot be later than 'End' date.", "red");
			onClick$trendsLstWeekAId();
			return;
		}
		
		
		topSellingItemLblId.setValue("");
		topSellingProdLblId.setValue("");
		topSellingStoreLblId.setValue("");
		hightestRevDayLblId.setValue("");
		
		setTrendsFields(start,end);
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
    private void setTrendsFields(final Calendar start, final Calendar end) {
        String startDateStr = MyCalendar.calendarToString(start, "yyyy-MM-dd");
        String endDateStr = MyCalendar.calendarToString(end, "yyyy-MM-dd");
        startDateStr = String.valueOf(startDateStr) + " 00:00:00";
        endDateStr = String.valueOf(endDateStr) + " 23:59:59";
        final Long userId = this.user.getUserId();
        final String inventoryId = this.retailProSalesDao.getHighestPurchasedSKU((long)userId, startDateStr, endDateStr);
        final String itemCategory = this.retailProSalesDao.getFrequentlyPurchasedItemCategory((long)userId, startDateStr, endDateStr);
        if (inventoryId != null && inventoryId.trim().length() > 0) {
            OptintelDashboardController.logger.debug("In Sales...based on Sales Price highest Purchased SKU is..." + inventoryId);
            final Long skuID = Long.parseLong(inventoryId);
            final SkuFile skuFile = this.skuFileDao.findRecordBySKU(skuID, userId);
            if (skuFile != null) {
                this.topSellingProdLblId.setValue(skuFile.getSku());
            }
        }
        if (itemCategory != null && itemCategory.trim().length() > 0) {
            OptintelDashboardController.logger.debug("In Sales...based on Sales Price frequently Purchased Item Category is..." + itemCategory);
            this.topSellingItemLblId.setValue(itemCategory);
        }
        final String storeNumber = this.retailProSalesDao.getMaxCountSKUProd((long)userId, startDateStr, endDateStr);
        if (storeNumber != null && storeNumber.trim().length() > 0) {
            OrganizationStores orgStores = null;
            OptintelDashboardController.logger.info("store number is " + storeNumber + " and >>>>>>>>>> org id is " + this.user.getUserOrganization().getUserOrgId());
            try {
                this.organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName("organizationStoresDao");
                orgStores = this.organizationStoresDao.getStore(this.user.getUserOrganization().getUserOrgId(), storeNumber);
                if (orgStores != null) {
                    final String storeName = orgStores.getStoreName();
                    this.topSellingStoreLblId.setValue((storeName != null) ? storeName : storeNumber);
                }
              
                else {
                    this.topSellingStoreLblId.setValue((storeNumber != null) ? storeNumber : "");
                }
            }
            catch (Exception e) {
                OptintelDashboardController.logger.error("Exception ::", (Throwable)e);
            }
        }
        final String highestRevenurDay = this.retailProSalesDao.getHighestRevenueDay((long)userId, startDateStr, endDateStr);
        if (highestRevenurDay != null && highestRevenurDay.trim().length() > 0) {
            this.hightestRevDayLblId.setValue(highestRevenurDay);
        }
    }
	
	//optintelRepImgId,optintelSettingImgId
	
	public void onClick$optintelRepImgId() {
		Redirect.goTo(PageListEnum.REPORT_OPTINTEL_REPORTS);
	}
	
	public void onClick$optintelSettingImgId() {
		Redirect.goTo(PageListEnum.ADMIN_POS_SETTINGS);
	}
	
	
	//actOneDayAId,actOneWeekAId,actOneMonthAId,actThreeMonthAId,actSixMonthAId,actOneYrAId
	
	private void setALinksStyle(A tempId, String type) {
		
		if(type.equals(_ACTIVITY)) {
			A activityAarr[] ={ actOneDayAId,actOneWeekAId,actOneMonthAId,actThreeMonthAId,actSixMonthAId,actOneYrAId};
			for (A a : activityAarr) {
				a.setSclass("dashboardMyLinks");
			}
			tempId.setSclass("dashboardMyLinksSelected");
		
		}
		else if(type.equals(_TRENDS)) {
			A trendsAarr[] ={ trendsLstWeekAId,trendsLstMonthAId,trendsLstThreeMnthsAId,trendsLastSixMonthsAId };
			for (A a : trendsAarr) {
				a.setSclass("dashboardMyLinks");
			}
			tempId.setSclass("dashboardMyLinksSelected");
		}
	} // 
	
	public void onClick$actOneDayAId() {
		setActivityDateDiffByDays(1, DATE_DIFF_TYPE_DAYS, _ACTIVITY); 
		setALinksStyle(actOneDayAId, _ACTIVITY);
	}
	
	public void onClick$actOneWeekAId() {
		setActivityDateDiffByDays(7, DATE_DIFF_TYPE_DAYS, _ACTIVITY);
		setALinksStyle(actOneWeekAId, _ACTIVITY);
	}
	
	public void onClick$actOneMonthAId() {
		setActivityDateDiffByDays(1, DATE_DIFF_TYPE_MONTHS, _ACTIVITY);
		setALinksStyle(actOneMonthAId, _ACTIVITY);
	}

	public void onClick$actThreeMonthAId() {
		setActivityDateDiffByDays(3,DATE_DIFF_TYPE_MONTHS,_ACTIVITY);
		setALinksStyle(actThreeMonthAId, _ACTIVITY);
	}
	
	public void onClick$actSixMonthAId() {
		setActivityDateDiffByDays(6, DATE_DIFF_TYPE_MONTHS,_ACTIVITY);
		setALinksStyle(actSixMonthAId, _ACTIVITY);
	}
	
	public void onClick$actOneYrAId() {
		setActivityDateDiffByDays(1,DATE_DIFF_TYPE_YEAR,_ACTIVITY);
		setALinksStyle(actOneYrAId, _ACTIVITY);
	}	


	
	
	
	private void setActivityDateDiffByDays(int timeDiff, String dateDiffType,String type) {
		try {
			
			//MyCalendar endDate = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
			//MyCalendar startDate = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
			
			TimeZone currTZ = (TimeZone) sessionScope.get("clientTimeZone");
			MyCalendar endDate = new MyCalendar(currTZ);
			endDate.set(MyCalendar.HOUR_OF_DAY, 23);
			endDate.set(MyCalendar.MINUTE, 59);
			endDate.set(MyCalendar.SECOND, 59);
			
			MyCalendar startDate = new MyCalendar(currTZ);
			startDate.set(MyCalendar.HOUR_OF_DAY, 00);
			startDate.set(MyCalendar.MINUTE, 00);
			startDate.set(MyCalendar.SECOND, 00);
			
			
			if(dateDiffType.equals(DATE_DIFF_TYPE_DAYS)) {
				startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - timeDiff);
				
			} else if (dateDiffType.equals(DATE_DIFF_TYPE_MONTHS)) { 
				startDate.set(MyCalendar.MONTH, endDate.get(MyCalendar.MONTH) - timeDiff);
			} 
			else if (dateDiffType.equals(DATE_DIFF_TYPE_YEAR)) {
				
				startDate.set(MyCalendar.YEAR, endDate.get(MyCalendar.YEAR) - timeDiff);
			}
			/*Date endDate = new Date();
			Date startDate = new Date();
			startDate.setDate(startDate.getDate() - days);*/

			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
			
			if(type.equals(_ACTIVITY)) {
				
				activityFilterEndDtBxId.setValue(endDate);
				activityFilterStartDtBxId.setValue(startDate);
				
				refreshActivityFields();
			} else if(type.equals(_TRENDS)) {
				
				trendsEndDateId.setValue(endDate);
				trendsStartDateId.setValue(startDate);
				
				refreshTrendsFields();
			} 
			
			
			//Compare dates
			Calendar pastStartCal = activityFilterStartDtBxId.getClientValue();
			Calendar pastEndCal = activityFilterStartDtBxId.getClientValue();
			if(dateDiffType.equals(DATE_DIFF_TYPE_DAYS)) {
				pastStartCal.set(MyCalendar.DATE, pastEndCal.get(MyCalendar.DATE) - timeDiff);
			} else if (dateDiffType.equals(DATE_DIFF_TYPE_MONTHS)) { 
				pastStartCal.set(MyCalendar.MONTH, pastEndCal.get(MyCalendar.MONTH) - timeDiff);
			} 
			else if (dateDiffType.equals(DATE_DIFF_TYPE_YEAR)) {
				pastStartCal.set(MyCalendar.YEAR, pastEndCal.get(MyCalendar.YEAR) - timeDiff);
			}
			pastEndCal.set(Calendar.DATE, pastEndCal.get(Calendar.DATE)-1 );
			
			pastStartCalStr = MyCalendar.calendarToString(pastStartCal, MyCalendar.FORMAT_YEARTODATE);
			pastEndCalStr = MyCalendar.calendarToString(pastEndCal, MyCalendar.FORMAT_YEARTODATE);

			pastStartCalStr = pastStartCalStr+" 00:00:00";
			pastEndCalStr = pastEndCalStr+" 23:59:59";
			
			//Set the value of past Date 
			pastStartAndEndDateLblId.setValue(MyCalendar.calendarToString(pastStartCal, MyCalendar.FORMAT_STDATE)+ " - " +
					MyCalendar.calendarToString(pastEndCal, MyCalendar.FORMAT_STDATE));
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	//trendsLstWeekAId,trendsLstMonthAId,trendsLstThreeMnthsAId,trendsLastSixMonthsAId;
	
	public void onClick$trendsLstWeekAId() {
		setActivityDateDiffByDays(7,DATE_DIFF_TYPE_DAYS,_TRENDS);
		setALinksStyle(trendsLstWeekAId, _TRENDS);
	}
	
	public void onClick$trendsLstMonthAId() {
		setActivityDateDiffByDays(1,DATE_DIFF_TYPE_MONTHS,_TRENDS);
		setALinksStyle(trendsLstMonthAId, _TRENDS);
	}

	public void onClick$trendsLstThreeMnthsAId() {
		setActivityDateDiffByDays(3,DATE_DIFF_TYPE_MONTHS,_TRENDS);
		setALinksStyle(trendsLstThreeMnthsAId, _TRENDS);
	}
	
	public void onClick$trendsLastSixMonthsAId() {
		setActivityDateDiffByDays(6,DATE_DIFF_TYPE_MONTHS,_TRENDS);
		setALinksStyle(trendsLastSixMonthsAId, _TRENDS);
	}	
	
	
	/* 
	 * 
	 * private Double totRevenueVal;
		private int noOfpurchaseVal;
		private Double revenueFromPromosVal;
		private int totPromoRedemptionsVal;
		private int returiningCustVal;
		private int newCustVal;
		
		pastDatesRevDivId,noOfPurDivId,revenueFrPromoDivId,totPromoRedtnsDivId,returnCustDivId,newCustDivId;
	 */
	
	public void onClick$compToPastChkBxId() {
		try {
			if(compToPastChkBxId.isChecked()) {
				
				
				/*pastDatesRevDivId.setVisible(true);
				noOfPurDivId.setVisible(true);
				revenueFrPromoDivId.setVisible(true);
				totPromoRedtnsDivId.setVisible(true);
				returnCustDivId.setVisible(true);
				newCustDivId.setVisible(true);*/
				
				pastDatesRevDivId.setVisible(false);
				noOfPurDivId.setVisible(false);
				revenueFrPromoDivId.setVisible(false);
				totPromoRedtnsDivId.setVisible(false);
				returnCustDivId.setVisible(false);
				newCustDivId.setVisible(false);
				//compareToDivId.setVisible(false);
				
				compareToDivId.setVisible(true);
				Long userId =user.getUserId();
				Object[] obj = retailProSalesDao.getTotalRevenueAndNoOfPurchase(userId,pastStartCalStr,pastEndCalStr, true);
				
				
				Double percentageChangeDouble = null;
				
				if(obj != null && obj.length > 0) {
					
					logger.debug("obj lenght" + obj.length);
					logger.debug("step 1 : obj1 : "+ obj[0] + " obj2 : "+ obj[1]);
					
					percentageChangeDouble = calcPercentageDifference(totRevenueVal,(Double)obj[0]);
					
					//totRevenueLblId.setValue(obj[0].toString());
					//noOfPurchaseLblId.setValue(obj[1].toString());
					if(percentageChangeDouble != null) {
						
						if(percentageChangeDouble != 0) {
							
							pastDatesRevDivId.setVisible(true);
							setPercentageChangeByDivId(pastDatesRevDivId, pastDatesRevLblId, percentageChangeDouble);
						} 
					} 
					
					percentageChangeDouble = null;
					percentageChangeDouble = calcPercentageDifference(noOfpurchaseVal,(Integer)obj[1]);
					logger.debug("No of purchase % diff : "+ percentageChangeDouble);
					
					if(percentageChangeDouble != null) {
						
						noOfPurDivId.setVisible(true);
						setPercentageChangeByDivId(noOfPurDivId, noOfPurLblId, percentageChangeDouble);
					} 
					
				}
				
				obj = null;
				percentageChangeDouble = null;
				obj = retailProSalesDao.getPromosTotalAndRevenue(userId,pastStartCalStr,pastEndCalStr, true);
				if(obj != null) {
					//revenueFromPromosVal = (Double)obj[0];
					//revenurFromPromosLblId.setValue(obj[0].toString());
					//totPromoRedemptionsVal = (Integer)obj[1];
					//totPromoRedemptionsLblId.setValue(obj[1].toString());
					logger.debug("step 2 : obj1 : "+ obj[0] + " obj2 : "+ obj[1]);
					
					percentageChangeDouble = calcPercentageDifference(revenueFromPromosVal,(Double)obj[0]);
					
					if(percentageChangeDouble != null) {
						revenueFrPromoDivId.setVisible(true);
						setPercentageChangeByDivId(revenueFrPromoDivId, pastRevFrPromLblId, percentageChangeDouble);
					} 
					
					percentageChangeDouble = null;
					percentageChangeDouble = calcPercentageDifference(totPromoRedemptionsVal,(Integer)obj[1]);
					if(percentageChangeDouble != null) {
						totPromoRedtnsDivId.setVisible(true);
						setPercentageChangeByDivId(totPromoRedtnsDivId, pastTotPromRedLblId,percentageChangeDouble);
					} 
					
					
				}
				
				obj = null;
				percentageChangeDouble = null;
				int pastRetCustCount = retailProSalesDao.getReturningCustomers(user.getUserId(),pastStartCalStr,pastEndCalStr);
				percentageChangeDouble = calcPercentageDifference(returiningCustVal,pastRetCustCount);
				if(percentageChangeDouble != null && percentageChangeDouble != 0) {
					returnCustDivId.setVisible(true);
					setPercentageChangeByDivId(returnCustDivId, pastReturnCustLblId ,percentageChangeDouble);
				} 
				/*if(obj != null) {
					//returiningCustVal = (Integer)obj[0];
					logger.debug("step 3 : obj0 : "+ obj[0]);
					
				}*/
				
				
				//obj = null;
				percentageChangeDouble = null;
				int newCount = retailProSalesDao.getNewCustomers(user.getUserId(),pastStartCalStr,pastEndCalStr);
				percentageChangeDouble = calcPercentageDifference(newCustVal,newCount);
				
				if(percentageChangeDouble != null && percentageChangeDouble != 0) {
					newCustDivId.setVisible(true);
					setPercentageChangeByDivId(newCustDivId, pastNewCustLblId,percentageChangeDouble);
				} 
				
				if(obj != null) {
					//returiningCustVal = (Integer)obj[0];
					logger.debug("step 4 : obj1 : "+ obj[0]);
					
				}
				
				
				
			} else {
				
				pastDatesRevDivId.setVisible(false);
				noOfPurDivId.setVisible(false);
				revenueFrPromoDivId.setVisible(false);
				totPromoRedtnsDivId.setVisible(false);
				returnCustDivId.setVisible(false);
				newCustDivId.setVisible(false);
				compareToDivId.setVisible(false);
				
				/*compareToDivId.setVisible(false);
				noOfPurDivId.setVisible(false);
				pastRevFrPromLblId.setVisible(false);
				pastTotPromRedLblId.setVisible(false);
				pastReturnCustLblId.setVisible(false);
				pastNewCustLblId.setVisible(false);*/
				
			}
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		}	
	}
	
	private Double calcPercentageDifference(Double newNum,Double orgNum) {
		logger.debug("Current : "+ newNum + " PAST VAL :" + orgNum);
		if(orgNum == 0.0) {
			return null;
		}
		logger.info("PERCENT DIFF : "+ ((newNum - orgNum) / orgNum) * 100);
		return ((newNum - orgNum) / orgNum) * 100; 
	}
	
	private Double calcPercentageDifference(int newNum,int orgNum) {
		logger.debug("Current : "+ newNum + " PAST VAL :" + orgNum);
		if(orgNum == 0) {
			return null;
		}
		Double temp = (newNum - orgNum) / Double.parseDouble(Integer.toString(orgNum));
		logger.debug("temp"+ temp);
		return temp * 100; 
	}
	
	// pastDatesRevDivId,noOfPurDivId,revenueFrPromoDivId,totPromoRedtnsDivId,returnCustDivId,newCustDivId
	/*private void  setPercentageChangeByDivId(Div div,Double val) {
		if(val  > 0) {
			div.appendChild(new Label("(" + val + "% "));
			div.appendChild(new Image("img/optDash/Upward_icn.png"));
			div.appendChild(new Label(")"));
		} else if (val < 0) {
			div.appendChild(new Label("(" + val + "% "));
			div.appendChild(new Image("img/optDash/Downward_icn.png"));
			div.appendChild(new Label(")"));
		}
	}*/
	
	private void  setPercentageChangeByDivId(Div div,Label divLbl,Double val) {
		
		logger.debug("DOUBLE VAL : "+ val);
		logger.debug("ROUNDED : " + Math.round(val));
		
		if(val  > 0) {
			
			div.setSclass("compareUp");	
			divLbl.setValue(Math.round(val)+"% ");
			divLbl.setSclass("compareArrow");
			//div.appendChild(new Label("(" + val + "% "));
			//div.appendChild(new Image("img/optDash/Upward_icn.png"));
			//div.appendChild(new Label(")"));
			
		} else if (val < 0) {
			
			div.setSclass("compareDown");	
			divLbl.setValue(Math.round(-1 * val)+"%");
			divLbl.setSclass("compareArrow");
			
			//div.appendChild(new Label("(" + val + "% "));
			//div.appendChild(new Image("img/optDash/Downward_icn.png"));
			//div.appendChild(new Label(")"));
		}
	}
	
	/*private void  setPercentageChangeByDivId(Div div,int val) {
		if(val  > 0) {
			div.appendChild(new Label("(" + val + "% "));
			div.appendChild(new Image("img/optDash/Upward_icn.png"));
			div.appendChild(new Label(")"));
		} else if (val < 0) {
			div.appendChild(new Label("(" + val + "% "));
			div.appendChild(new Image("img/optDash/Downward_icn.png"));
			div.appendChild(new Label(")"));
		}
	}
	
	private void  setPercentageChangeByDivId(Div div,Label divLbl,int val) {
		
		logger.debug("INT VAL : "+ val);
		
		if(val  > 0) {
			
			div.setSclass("compareUp");	
			divLbl.setValue(val+"%");
			divLbl.setSclass("compareArrow");
		} else if (val < 0) {
			
			div.setSclass("compareDown");	
			divLbl.setValue(-1 * val+"%");
			divLbl.setSclass("compareArrow");
		}
	}*/
	
	public void onClick$viewLogsAId() {
		try {
			 List<POSFileLogs> list = posFileLogDao.findAllById(GetUser.getUserId());
			 viewLogsWinId.setVisible(true);
			 viewLogsWinId.doHighlighted();
			 viewLogsWinId.setPosition("center");
			
			 if(list != null && list.size() > 0) {
				 logger.debug("Log size : "+ list.size());
				 
				 
				 Rows rows = new Rows();
				 
				 for (POSFileLogs posFileLogs : list) {
					 
					 Row row = new Row();
					 Label lbl = new Label(posFileLogs.getFileName());
					 lbl.setParent(row);
					 
					 lbl = new Label(posFileLogs.getFileType());
					 lbl.setParent(row);
					 
					 lbl = new Label(MyCalendar.calendarToString(posFileLogs.getFetchedTime(),MyCalendar.FORMAT_SCHEDULE));
					 lbl.setParent(row);
					 
					 row.setParent(rows);
				}
				 
				 rows.setParent(viewLogsWinId$viewLogsGridId);
			 } else {
				 
				 viewLogsWinId$viewLogsGridId.setEmptyMessage("No logs found.");
				 
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} 
	
  

	
}
