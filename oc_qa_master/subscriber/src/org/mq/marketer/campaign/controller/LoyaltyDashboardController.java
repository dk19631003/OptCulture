package org.mq.marketer.campaign.controller;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SparkBaseTransactions;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SparkBaseTransactionsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class LoyaltyDashboardController extends GenericForwardComposer {

	
	private MyDatebox activityFilterEndDtBxId,activityFilterStartDtBxId,trendsStartDateId,trendsEndDateId;
	private A actOneDayAId,actOneWeekAId,actOneMonthAId,actThreeMonthAId,actSixMonthAId,actOneYrAId;
	private A trendsLstWeekAId,trendsLstMonthAId,trendsLstThreeMnthsAId,trendsLastSixMonthsAId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private static final String _TRENDS = "trends";
	private static final String _ACTIVITY = "activity";
	
	private static final String DATE_DIFF_TYPE_DAYS = "days";
	private static final String DATE_DIFF_TYPE_MONTHS = "months";
	private static final String DATE_DIFF_TYPE_YEAR = "year";
	
	private MailingListDao mailingListDao;
	private ContactsLoyaltyDao contactsLoyaltyDao; 
	private RetailProSalesDao retailProSalesDao;
	private MailingList posMailingList;
	private SparkBaseTransactions sparkBaseTransactions;
//	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // HH:mm:ss
//	private DateFormat dateFormat2 = new SimpleDateFormat(MyCalendar.FORMAT_STDATE);
	private Users user;
	private String pastStartCalStr,pastEndCalStr;
	
	public LoyaltyDashboardController() {
		// TODO Auto-generated constructor stub
		}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp); 
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Loyalty Dashboard", Constants.STRING_NILL, style, true);
		
		user = GetUser.getUserObj();
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		posMailingList = mailingListDao.findPOSMailingList(user);
		retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		// Set the Activity Dates
		
		onClick$actOneWeekAId();
		onClick$trendsLstWeekAId();
		
		/*TimeZone currTZ = (TimeZone) sessionScope.get("clientTimeZone");
		
		MyCalendar endDate = new MyCalendar(currTZ);
		endDate.set(MyCalendar.HOUR_OF_DAY, 23);
		endDate.set(MyCalendar.MINUTE, 59);
		endDate.set(MyCalendar.SECOND, 59);
		
		endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) -1);
		activityFilterEndDtBxId.setValue(endDate);
		
		MyCalendar startDate = new MyCalendar(currTZ);
		startDate.set(MyCalendar.HOUR_OF_DAY, 00);
		startDate.set(MyCalendar.MINUTE, 00);
		startDate.set(MyCalendar.SECOND, 00);
		
		startDate.set(MyCalendar.DATE, startDate.get(MyCalendar.DATE)-7);
		activityFilterStartDtBxId.setValue(startDate.getTime());
		
		// Set the Trends dates
		MyCalendar trendsEndDate = new MyCalendar(currTZ);
		trendsEndDate.set(MyCalendar.HOUR_OF_DAY, 23);
		trendsEndDate.set(MyCalendar.MINUTE, 59);
		trendsEndDate.set(MyCalendar.SECOND, 59);

		trendsEndDate.set(MyCalendar.DATE, trendsEndDate.get(MyCalendar.DATE)-1);
		trendsEndDateId.setValue(trendsEndDate.getTime());
		logger.info("trendsEndDate getDate is >>>"+trendsEndDate.get(Calendar.DATE));
		
		MyCalendar trendStartDate = new MyCalendar(currTZ);
		trendStartDate.set(MyCalendar.HOUR_OF_DAY, 00);
		trendStartDate.set(MyCalendar.MINUTE, 00);
		trendStartDate.set(MyCalendar.SECOND, 00);
		
		trendStartDate.set(MyCalendar.DATE, trendStartDate.get(MyCalendar.DATE)-7);
		trendsStartDateId.setValue(trendStartDate.getTime());
	*/	
		// Initializes the Values
		//TODO
		/*setActivityFields(activityFilterStartDtBxId.getValue(),activityFilterEndDtBxId.getValue());
		setTrendsFields(trendsStartDateId.getValue(),trendsEndDateId.getValue());*/
		
		//setActivityFields(activityFilterStartDtBxId.getServerValue(),activityFilterEndDtBxId.getServerValue());
		//setTrendsFields(trendsStartDateId.getServerValue(),trendsEndDateId.getServerValue());
		
	}
	
	
//	private Long totalShoppersLong = 0l ;
	private Label loyaltyOptinsLblId,loyaltyOptinsPersentLblId,loyaltyShoppersLblId,loyaltyShoppersPersentLblId,
					giftcardShoppersPersentLblId,loyaltyRevenueLblId,loyaltyRevenuePersentLblId,
					giftcardRevenueLblId,noofRedemptionsLblId,pointsReedemedLblId,amountReedemedLblId,noofIssuancesLblId,giftCardISSLblId,giftcardRevenuePersentLblId,noofPointIssuancesLblId;
	
	private void setActivityFields(Calendar start,Calendar end) {
		try {

			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			String startCalStr = MyCalendar.calendarToString(start, MyCalendar.FORMAT_YEARTODATE);
			String endCalStr  =  MyCalendar.calendarToString(end, MyCalendar.FORMAT_YEARTODATE);

			startCalStr = startCalStr+" 00:00:00";
			endCalStr= endCalStr+" 23:59:59";

			Long totalShoppersLong = 0l ;
			Double totalRevenueDbl = null;
			logger.info("list id : "+ posMailingList.getListId());
			Long userId = user.getUserId();
			//Total Shoppers and Revenue
			List<Map<String, Object>>  listMap = retailProSalesDao.findTotalShoppers (userId,startCalStr,endCalStr);
			/*Object[]  obj = retailProSalesDao.findTotalShoppers (posMailingList.getListId(),dateFormat2.format(startDate)(startDate, MyCalendar.FORMAT_DATETIME_STYEAR),
					 																		endCalStr);*/

			if(listMap != null && listMap.size() >0) {
				for (Map<String, Object> eachMap : listMap) {
					totalShoppersLong = Long.parseLong(eachMap.containsKey("CONTACTID") && eachMap.get("CONTACTID") != null ? eachMap.get("CONTACTID").toString() : "0" );
					totalRevenueDbl = Double.parseDouble(eachMap.containsKey("REVENUE") && eachMap.get("REVENUE") != null ? eachMap.get("REVENUE").toString() : "0" );
				}
			}
			logger.debug(" Total shoppers count is .."+totalShoppersLong);
			logger.debug(" Total Revenue  is .."+totalRevenueDbl);


			//Loyalty Optins
			//Long totalLoyltyOptins = findTotalLoyaltyOptins(userId,startCalStr,endCalStr);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Long totalLoyltyOptins = loyaltyTransactionChildDao.findTotalOCLoyaltyOptins(userId,startCalStr,endCalStr);
			logger.debug("Loyalty Optins is .."+totalLoyltyOptins);

			if(totalLoyltyOptins != null && totalLoyltyOptins != 0) {
				loyaltyOptinsLblId.setValue(""+totalLoyltyOptins);
				if(totalShoppersLong != 0) {
					loyaltyOptinsPersentLblId.setValue(""+Utility.getPercentage(totalLoyltyOptins, totalShoppersLong, 2));
					//					loyaltyOptinsPersentLblId.setValue(""+(totalLoyltyOptins*100/totalShoppersLong));
				}else {
					loyaltyOptinsPersentLblId.setValue("0");
				}
				//loyaltyOptinsPersentLblId.setValue(""+calcPercentageDifference(totalShoppersLong, totalLoyltyOptins));
			}else {
				loyaltyOptinsLblId.setValue("0");
				loyaltyOptinsPersentLblId.setValue("0");
				//				loyaltyOptinsPersentLblId.setValue(""+calcPercentageDifference(totalShoppersLong, Long.parseLong(loyaltyOptinsLblId.getValue().trim())));
			}
			
			
			//Loyalty Shoppers
			Long totalLtyShoppers = 0L;
			//calling findTotalLoyaltyCardShoppers which calculates total Loyalty card shoppers for Sparkbase & OC loyalty.
			//totalLtyShoppers = findTotalLoyaltyCardShoppers(userId,startCalStr,endCalStr);
			totalLtyShoppers = retailProSalesDao.findTotalCountByServiceTypeAndRewardFlag(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,startCalStr,endCalStr);
			logger.info(" totalLtyShoppers :: "+totalLtyShoppers);
			//Setting total shoppers
			if(totalLtyShoppers != 0){
				
				Long totalShoppersVisitsLong = 0l ;
				List<Map<String, Object>>  retMap = retailProSalesDao.findTotalShoppersVisits(userId, startCalStr,endCalStr);
				/*Object[]  obj = retailProSalesDao.findTotalShoppers (posMailingList.getListId(),dateFormat2.format(startDate)(startDate, MyCalendar.FORMAT_DATETIME_STYEAR),
						 																		endCalStr);*/

				if(retMap != null && retMap.size() >0) {
					for (Map<String, Object> eachMap : retMap) {
						totalShoppersVisitsLong = Long.parseLong(eachMap.containsKey("visits") && eachMap.get("visits") != null ? eachMap.get("visits").toString() : "0" );
					}
				}
				loyaltyShoppersLblId.setValue(""+totalLtyShoppers);
				logger.info(" totalShoppersvisits :: "+totalShoppersVisitsLong);
				if(totalShoppersVisitsLong != 0) {
					loyaltyShoppersPersentLblId.setValue(""+Utility.getPercentage(totalLtyShoppers, totalShoppersVisitsLong, 2));
				}
			}else {
				loyaltyShoppersLblId.setValue("0");
				loyaltyShoppersPersentLblId.setValue("0");
			}

			//Loyalty Revenue
			Double totalLtyRevenue = 0.0;
			//Double amountReedemedDbl = 0.0;
			//Double sbLtyRevenue = retailProSalesDao.findTotalRevenueByServiceType(userId,OCConstants.LOYALTY_SERVICE_TYPE_SB,startCalStr, endCalStr);
			totalLtyRevenue = findOcTotalRevenue(userId,startCalStr,endCalStr);

			/*if((sbLtyRevenue != null && sbLtyRevenue != 0) && (ocLtyRevenue != null && ocLtyRevenue != 0)){
				totalLtyRevenue = sbLtyRevenue + ocLtyRevenue;
			}
			else if (sbLtyRevenue != null && sbLtyRevenue != 0){
				totalLtyRevenue = sbLtyRevenue ;
			}
			else if (ocLtyRevenue != null && ocLtyRevenue != 0){
				totalLtyRevenue = ocLtyRevenue;
			}*/
			
			
			if( totalLtyRevenue != 0.0) {
				loyaltyRevenueLblId.setValue(decimalFormat.format(totalLtyRevenue));

				if(totalRevenueDbl != null && totalRevenueDbl != 0) {
					loyaltyRevenuePersentLblId.setValue(""+Utility.getPercentage(totalLtyRevenue, totalRevenueDbl, 2));
				}else {
					loyaltyRevenuePersentLblId.setValue("0");
				}
			}else {
				loyaltyRevenueLblId.setValue("0");
				loyaltyRevenuePersentLblId.setValue("0");
			}

			//amountReedemedDbl = sbLtyRevenue == null ? 0.0 : sbLtyRevenue;

			//GiftCard Revenue
			Double totalGiftCardRevenue = 0.0;
			totalGiftCardRevenue = findTotalGiftCardRevenue(userId,startCalStr,endCalStr);
			logger.info("Gift Card Revenue is .."+totalGiftCardRevenue);
			if(totalGiftCardRevenue != 0.0) {
				giftcardRevenueLblId.setValue(decimalFormat.format(totalGiftCardRevenue));
				if(totalRevenueDbl != null && totalRevenueDbl != 0) {
					giftcardRevenuePersentLblId.setValue(""+Utility.getPercentage(totalGiftCardRevenue, totalRevenueDbl, 2));
				}else {
					giftcardRevenuePersentLblId.setValue("0");
				}
				//amountReedemedDbl = amountReedemedDbl + totalGiftCardRevenue;
			}else {
				giftcardRevenueLblId.setValue("0");
				giftcardRevenuePersentLblId.setValue("0");
			}

			//No.of Loyalty Redemption
			Long totalRedemption = 0L;
			totalRedemption = findTotalRedemption(userId,startCalStr,endCalStr);
			if(totalRedemption != null && totalRedemption!=0){
			noofRedemptionsLblId.setValue(totalRedemption+"");
			}else{
				noofRedemptionsLblId.setValue("0");
			}
			
			//Points Issued in Loyalty
			Long ocPtsIssued = 0L;
			ocPtsIssued = findPointsIssuedOC(userId,startCalStr,endCalStr);
			if(ocPtsIssued !=null && ocPtsIssued != 0) {
				noofPointIssuancesLblId.setValue(ocPtsIssued+"");
			}else noofPointIssuancesLblId.setValue("0");
			
			
			
			
			//Amount Issued in Loyalty
			Double ocAmtIssued = findAmountIssuedOC(userId,startCalStr,endCalStr);
			if(ocAmtIssued !=null && ocAmtIssued != 0.0) {
				noofIssuancesLblId.setValue(decimalFormat.format(ocAmtIssued));
			}else noofIssuancesLblId.setValue("0");


			//Amount Redeemed in loyalty
			//logger.info(" amountReedemedDbl is .."+ amountReedemedDbl);

			
			//For OC type points redeem 
			LoyaltyTransactionChildDao loyaltyTransactionChildDao1 = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Long ocPtsRedeemed = loyaltyTransactionChildDao1.findPointsRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,startCalStr, endCalStr);
			//if (ocPtsRedeemed == null){
				//ocPtsRedeemed = 0L;
			//}
			//amountReedemedDbl = amountReedemedDbl + ocAmtRedeemed ;
			if(ocPtsRedeemed !=null && ocPtsRedeemed != 0) {
				pointsReedemedLblId.setValue(ocPtsRedeemed+"");
			}else pointsReedemedLblId.setValue("0");

			//For OC type amount redeem 
			/*LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Double ocAmtRedeemed = loyaltyTransactionChildDao.findAmountRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,startCalStr, endCalStr);
			if (ocAmtRedeemed == null){
				ocAmtRedeemed = 0.0;
			}
			//amountReedemedDbl = amountReedemedDbl + ocAmtRedeemed ;
			if(ocAmtRedeemed != 0.0) {
				amountReedemedLblId.setValue(decimalFormat.format(ocAmtRedeemed));
			}else amountReedemedLblId.setValue("0");*/
			
			Double ocAmtRedeemed = 0.0;
			ocAmtRedeemed = findAmountRedeemedOC(userId,startCalStr, endCalStr);
			if( ocAmtRedeemed !=null &&  ocAmtRedeemed !=0){
				amountReedemedLblId.setValue(decimalFormat.format(ocAmtRedeemed));
			}else {
				amountReedemedLblId.setValue("0");
			}
			
			//Gift-Card Issuance
			LoyaltyTransactionChildDao loyaltyTransactionChildDao3 = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Long totalGftIssuance = 0L;
			totalGftIssuance = loyaltyTransactionChildDao3.findTotGiftIssuance(userId,startCalStr,endCalStr);
			giftCardISSLblId.setValue(totalGftIssuance+"");

		} catch(Exception e) {
			logger.error("Exception ::", e);
		}
	}//setActivityFeilds


	private Double getTotalAmtIssued(Long userId, String startCalStr,String endCalStr) {
		Double totAmtIssued = 0.0;
		try {
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		//SparkBaseTransactionsDao sparkBaseTransactionsDao = (SparkBaseTransactionsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_TRANSACTIONS_DAO);
		totAmtIssued = loyaltyTransactionChildDao.getTotAmtIssued(userId,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,startCalStr, endCalStr);
		//totAmtIssued += sparkBaseTransactionsDao.getTotAmtIssued(userId,startCalStr, endCalStr);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return totAmtIssued;
	}
	
	private Double getTotalPointsIssued(Long userId, String startCalStr,String endCalStr) {
		Double totPointsIssued = 0.0;
		try {
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		//SparkBaseTransactionsDao sparkBaseTransactionsDao = (SparkBaseTransactionsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_TRANSACTIONS_DAO);
		totPointsIssued = loyaltyTransactionChildDao.getTotPointsIssued(userId,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,startCalStr, endCalStr);
		//totAmtIssued += sparkBaseTransactionsDao.getTotAmtIssued(userId,startCalStr, endCalStr);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return totPointsIssued;
	}
	
	private Long findPointsIssuedOC(Long userId, String startCalStr,String endCalStr) {
		logger.info("entered findPointsRedeemedOC");
		
		Long ocPtsIssued = 0L;
		try{
		//Long  sbNumberOfReedmption = retailProSalesDao.findReedmptionByTypes(userId, startCalStr, endCalStr);
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		Long ltyPtsIssuance = loyaltyTransactionChildDao.findLtyPtsearnedFromIssuance(userId,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,startCalStr, endCalStr);
	    Long ltyPtsBonus =  loyaltyTransactionChildDao.findLtyPtsearnedFromBonus(userId,OCConstants.LOYALTY_TRANS_TYPE_BONUS,startCalStr, endCalStr);
	    Long ltyPtsAdjustment = loyaltyTransactionChildDao.findLtyPtsearnedFromAdjustment(userId,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT,startCalStr, endCalStr);
		if((ltyPtsIssuance != null && ltyPtsIssuance != 0) && (ltyPtsBonus != null && ltyPtsBonus != 0) && (ltyPtsAdjustment != null && ltyPtsAdjustment != 0)){
			ocPtsIssued = ltyPtsIssuance + ltyPtsBonus + ltyPtsAdjustment ;
		}
		else if((ltyPtsIssuance != null && ltyPtsIssuance != 0) && (ltyPtsBonus != null && ltyPtsBonus != 0)){
			ocPtsIssued = ltyPtsIssuance + ltyPtsBonus;
		}
		else if((ltyPtsIssuance != null && ltyPtsIssuance != 0) && (ltyPtsAdjustment != null && ltyPtsAdjustment != 0)){
			ocPtsIssued = ltyPtsIssuance + ltyPtsAdjustment;
		}
		else if((ltyPtsBonus != null && ltyPtsBonus != 0) && (ltyPtsAdjustment != null && ltyPtsAdjustment != 0)){
			ocPtsIssued = ltyPtsBonus + ltyPtsAdjustment;
		}
		else if(ltyPtsIssuance != null && ltyPtsIssuance != 0){
			ocPtsIssued = ltyPtsIssuance ;
		}
		else if(ltyPtsBonus != null && ltyPtsBonus != 0){
			ocPtsIssued = ltyPtsBonus;
		}
		else if(ltyPtsAdjustment != null && ltyPtsAdjustment != 0){
			ocPtsIssued = ltyPtsAdjustment;
		}
		logger.info("TotalPointsIssued is .."+ocPtsIssued);
		/*if( ocPtsIssued !=0){
			noofPointIssuancesLblId.setValue(ocPtsIssued+"");
		}else {
			noofPointIssuancesLblId.setValue("0");
		}*/
		logger.info("completed findPointsIssuedOC");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return ocPtsIssued;
	}//findPointsIssuedOC
	
	private Double findAmountIssuedOC(Long userId, String startCalStr,String endCalStr) {
		logger.info("entered findAmountIssuedOC");
		
		Double ocAmtIssued = 0.0;
		try{
		//Long  sbNumberOfReedmption = retailProSalesDao.findReedmptionByTypes(userId, startCalStr, endCalStr);
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		Double ltyAmtIssuance = loyaltyTransactionChildDao.findLtyAmtearnedFromIssuance(userId,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,startCalStr, endCalStr);
	    Double ltyAmtBonus =  loyaltyTransactionChildDao.findLtyAmtearnedFromBonus(userId,OCConstants.LOYALTY_TRANS_TYPE_BONUS,startCalStr, endCalStr);
	    Double ltyAmtAdjustment = loyaltyTransactionChildDao.findLtyAmtearnedFromAdjustment(userId,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT,startCalStr, endCalStr);
		if((ltyAmtIssuance != null && ltyAmtIssuance != 0) && (ltyAmtBonus != null && ltyAmtBonus != 0) && (ltyAmtAdjustment != null && ltyAmtAdjustment != 0)){
			ocAmtIssued = ltyAmtIssuance + ltyAmtBonus + ltyAmtAdjustment ;
		}
		else if((ltyAmtIssuance != null && ltyAmtIssuance != 0) && (ltyAmtBonus != null && ltyAmtBonus != 0)){
			ocAmtIssued = ltyAmtIssuance + ltyAmtBonus;
		}
		else if((ltyAmtIssuance != null && ltyAmtIssuance != 0) && (ltyAmtAdjustment != null && ltyAmtAdjustment != 0)){
			ocAmtIssued = ltyAmtIssuance + ltyAmtAdjustment;
		}
		else if((ltyAmtBonus != null && ltyAmtBonus != 0) && (ltyAmtAdjustment != null && ltyAmtAdjustment != 0)){
			ocAmtIssued = ltyAmtBonus + ltyAmtAdjustment;
		}
		else if(ltyAmtIssuance != null && ltyAmtIssuance != 0){
			ocAmtIssued = ltyAmtIssuance ;
		}
		else if(ltyAmtBonus != null && ltyAmtBonus != 0){
			ocAmtIssued = ltyAmtBonus;
		}
		else if(ltyAmtAdjustment != null && ltyAmtAdjustment != 0){
			ocAmtIssued = ltyAmtAdjustment;
		}
		logger.info("TotalAmountIssued is .."+ocAmtIssued);
		/*if( ocAmtIssued !=0){
			noofIssuancesLblId.setValue(ocAmtIssued+"");
		}else {
			noofIssuancesLblId.setValue("0");
		}*/
		logger.info("completed findAmountIssuedOC");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return ocAmtIssued;
	}//findAmountIssuedOC
	
	
	private Double findAmountRedeemedOC(Long userId, String startCalStr,String endCalStr) {
		logger.info("entered findAmountRedeemedOC");
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		Double ocAmtRedeemed = 0.0;
		try{
		//Long  sbNumberOfReedmption = retailProSalesDao.findReedmptionByTypes(userId, startCalStr, endCalStr);
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		Double ltyAmountRedeemed = loyaltyTransactionChildDao.findLtyAmountRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,startCalStr, endCalStr);
	    Double giftAmountRedeemed =  loyaltyTransactionChildDao.findGiftAmountRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,startCalStr, endCalStr);

		if((ltyAmountRedeemed != null && ltyAmountRedeemed != 0) && (giftAmountRedeemed != null && giftAmountRedeemed != 0)){
			ocAmtRedeemed = ltyAmountRedeemed + giftAmountRedeemed;
		}
		else if(ltyAmountRedeemed != null && ltyAmountRedeemed != 0){
			ocAmtRedeemed = ltyAmountRedeemed ;
		}
		else if(giftAmountRedeemed != null && giftAmountRedeemed != 0){
			ocAmtRedeemed = giftAmountRedeemed;
		}
		logger.info("TotalAmtRedeemed is .."+ocAmtRedeemed);
		/*if( ocAmtRedeemed !=0){
			amountReedemedLblId.setValue(decimalFormat.format(ocAmtRedeemed));
		}else {
			amountReedemedLblId.setValue("0");
		}*/
		logger.info("completed findAmountRedeemedOC");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return ocAmtRedeemed;
	}//findAmountRedeemedOC


	public void onChange$activityFilterStartDtBxId() {
		//logger.info("--1--");
		refreshActivityFields();
		
	}
	//calling findTotalRedemption which calculates total redemption for both sparkbase & ocloyalty 
	public void onChange$activityFilterEndDtBxId() {
		//logger.info("--2---");
		refreshActivityFields();
	}
	
	private void refreshActivityFields() {
		logger.info("refresh activity feilds");
		try {
			//TODO
			/*Date start = activityFilterStartDtBxId.getValue();
			Date end = activityFilterEndDtBxId.getValue();*/
			
			Calendar  start = activityFilterStartDtBxId.getClientValue();
			Calendar end = activityFilterEndDtBxId.getClientValue();
			
			if(end.before(start)) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				onClick$actOneDayAId();
				return;
			}
			
			/*totRevenueLblId.setValue("");
			noOfPurchaseLblId.setValue("");
			revenurFromPromosLblId.setValue("");
			totPromoRedemptionsLblId.setValue("");
			newCustLblId.setValue("");
			returningCustLblId.setValue("");*/
			
			setActivityFields(start,end);
			
			// Check past date field.
			onCheck$compToPastChkBxId();
		} catch(Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onChange$trendsEndDateId() {
		refreshTrendsFields();
	}
	
	public void onChange$trendsStartDateId() {
		refreshTrendsFields();
	}
	
	private void refreshTrendsFields() {
		//TODO need to check 
		Calendar  endCal = trendsEndDateId.getClientValue();
		Calendar startCal = trendsStartDateId.getClientValue();
		
		if(endCal.before(startCal)) {
			MessageUtil.setMessage("'Start' date cannot be later than 'End' date.", "red");
			onClick$trendsLstWeekAId();
			return;
		}
		
		
		/*topSellingItemLblId.setValue("");
		topSellingProdLblId.setValue("");
		topSellingStoreLblId.setValue("");
		hightestRevDayLblId.setValue("");*/
		
		setTrendsFields(startCal,endCal);
		
	}
	
	private Label topLoyaltyOptinLocationLblId,topLoyaltyOptinEmpIDLblId,avgSpendingsByLoyaltyShopperLblId,avgSpendingsByLoyaltyShopperPersentLblId;
	
	private void setTrendsFields(Calendar start,Calendar end) {
		
		try {
		//String sku = retailProSalesDao.getHighestPurchasedSKU(posMailingList.getListId(),dateFormat.format(start),dateFormat.format(end));
		
		String trendsStartDate = MyCalendar.calendarToString(start, MyCalendar.FORMAT_YEARTODATE);
		String trendEndDate = MyCalendar.calendarToString(end, MyCalendar.FORMAT_YEARTODATE);
		trendsStartDate = trendsStartDate+" 00:00:00"; 
		trendEndDate = trendEndDate+" 23:59:59";
		
		//Top Loyalty Opt-in Location : 
		Long userId  =   user.getUserId() ;
		
		String topLoyOptLoc = contactsLoyaltyDao.findTopLocation(Constants.TRENDS_LOACTION_ID ,OCConstants.LOYALTY_SERVICE_TYPE_OC,userId,
																	trendsStartDate,trendEndDate);
		if(topLoyOptLoc != null ) {
			topLoyaltyOptinLocationLblId.setValue(topLoyOptLoc);
		}else {
			topLoyaltyOptinLocationLblId.setValue("");
		}
		
		
		/*//Top Loyalty Opt-in Emp. ID :
		String  topLoyEmpLoc = contactsLoyaltyDao.findTopLocation(Constants.TRENDS_EMP_ID ,userId,
																	trendsStartDate,trendEndDate);
		if(topLoyEmpLoc != null ) {
			topLoyaltyOptinEmpIDLblId.setValue(topLoyEmpLoc);
		}else {
			topLoyaltyOptinEmpIDLblId.setValue("");
		}*/
		
		//Top Gift Opt-in Location : 
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				String topGiftOptLoc = loyaltyTransactionChildDao.findTopLocation(userId,trendsStartDate,trendEndDate);
				if(topLoyOptLoc != null ) {
					topLoyaltyOptinEmpIDLblId.setValue(topGiftOptLoc);
				}else {
					topLoyaltyOptinEmpIDLblId.setValue("");
				}
		
		
		//Avg. Spendings by Loyalty Shopper :
//		Long userId = user.getParentUser() == null ? user.getUserId() :user.getParentUser().getUserId();
		//Object[] obj = retailProSalesDao.findAvgSpendTimeByLoyShopper(trendsStartDate,trendEndDate, userId);
		List<Map<String, Object>> tempList = retailProSalesDao.findAvgSpendTimeByLoyShopper(trendsStartDate,trendEndDate, userId);
		int tempInt = 0;
		Double tempDouble= 0.0;
		if(tempList != null && tempList.size() >0 ) {
			for (Map<String, Object> eachMap : tempList) {
				
				tempInt = Integer.parseInt(eachMap.containsKey("count") ?eachMap.get("count").toString() : "0" );
				
				tempDouble = Double.parseDouble(eachMap.containsKey("price") && eachMap.get("price") != null ?eachMap.get("price").toString() : "0" );
				break;
			}
			
			/*tempInt = (Integer)obj[0];
			tempDouble = (Double)obj[1];*/
		}
		if(tempInt !=0) {
			avgSpendingsByLoyaltyShopperPersentLblId.setValue(Utility.getPercentage(Double.parseDouble(Integer.toString(tempInt)), tempDouble, 2));
		}
		avgSpendingsByLoyaltyShopperLblId.setValue(""+tempInt);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
			
		}
		
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
			logger.info("setActivityDateDiffByDays");
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
				logger.error("Exception ::", e);
			}
		}
	
	
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
		
		
		private Checkbox compToPastChkBxId;
		private Div compareToDivId,pastDatesRevDivId,loyaltyShoppersCompDivId,
					loyaltyRevenueCompDivId,giftcardRevenueCompDivId,noOfRedemptionsCompDivId,amountReedemedCompDivId,
					noOfIssuancesCompDivId,noOfPointIssuancesCompDivId,pointsReedemedCompDivId,giftCardIssCompDivId;
		
		private Label amountReedemedCompLblId,noOfRedemptionsCompLblId,giftcardRevenueCompLblId,loyaltyRevenueCompLblId,
						loyaltyShoppersCompLblId,pastDatesRevLblId,noOfIssuancesCompLblId,noOfPointIssuancesCompLblId,pointsReedemedCompLblId,giftCardIssCompLblId;
		private Label pastStartAndEndDateLblId;
		
		
		public void onCheck$compToPastChkBxId() { 

			try {
			pastDatesRevDivId.setVisible(false);
			loyaltyShoppersCompDivId.setVisible(false);
			loyaltyRevenueCompDivId.setVisible(false);
			giftcardRevenueCompDivId.setVisible(false);
			noOfRedemptionsCompDivId.setVisible(false);
			noOfPointIssuancesCompDivId.setVisible(false);
			pointsReedemedCompDivId.setVisible(false);
			amountReedemedCompDivId.setVisible(false);
			compareToDivId.setVisible(false);
			noOfIssuancesCompDivId.setVisible(false);
			giftCardIssCompDivId.setVisible(false);



			if(compToPastChkBxId.isChecked()) {

				compareToDivId.setVisible(true);
				/*//Set the value of past Date 
				pastStartAndEndDateLblId.setValue(MyCalendar.calendarToString(pastStartCal, MyCalendar.FORMAT_STDATE)+ " - " +
						MyCalendar.calendarToString(pastEndCal, MyCalendar.FORMAT_STDATE));*/



				//Past Loyalty Optins
				Long userId =  user.getUserId();
				//Long totalLoyltyOptins = findTotalLoyaltyOptins(userId, pastStartCalStr, pastEndCalStr);
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				Long totalLoyltyOptins = loyaltyTransactionChildDao.findTotalOCLoyaltyOptins(userId,pastStartCalStr,pastEndCalStr);
                logger.debug("Past Loyalty Optins is..."+totalLoyltyOptins);
                if(totalLoyltyOptins != null) {
					Double tempDbl =  calcPercentageDifference(Long.parseLong(loyaltyOptinsLblId.getValue()), totalLoyltyOptins);
					logger.debug("tempDbl is..."+tempDbl);
					if(tempDbl != null && tempDbl != 0.0) {
						pastDatesRevDivId.setVisible(true);
						setPercentageChangeByDivId(pastDatesRevDivId, pastDatesRevLblId, tempDbl);
					}

				}

				//Loyalty Shoppers
				Long totalLoyaltyShoppers = 0L;
				//calling findTotalLoyaltyCardShoppers which calculates total Loyalty card shoppers for Sparkbase & OC loyalty.
				//totalLoyaltyShoppers = findTotalLoyaltyCardShoppers(userId, pastStartCalStr, pastEndCalStr);
				totalLoyaltyShoppers = retailProSalesDao.findTotalCountByServiceTypeAndRewardFlag(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,pastStartCalStr,pastEndCalStr); 
				if(totalLoyaltyShoppers != null) {
					Double tempDbl =  calcPercentageDifference(Long.parseLong(loyaltyShoppersLblId.getValue()), totalLoyaltyShoppers);
					logger.info("Loyalty shoppers Div label value ::"+tempDbl);
					if(tempDbl != null && tempDbl != 0.0) {
						loyaltyShoppersCompDivId.setVisible(true);
						setPercentageChangeByDivId(pastDatesRevDivId, loyaltyShoppersCompLblId, tempDbl);
					}
				}
				
				//Loyalty Revenue
				Double totalLoyaltyRevenue = 0.0 ;
				//Double sbLtyRevenue = retailProSalesDao.findTotalRevenueByServiceType(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,pastStartCalStr, pastEndCalStr);
				totalLoyaltyRevenue = findOcTotalRevenue(userId, pastStartCalStr, pastEndCalStr);
				
				/*if((sbLtyRevenue != null && sbLtyRevenue != 0) && (ocLoyaltyRevenue != null && ocLoyaltyRevenue != 0)){
					totalLoyaltyRevenue = sbLtyRevenue + ocLoyaltyRevenue;
				}
				else if (sbLtyRevenue != null && sbLtyRevenue != 0){
					totalLoyaltyRevenue = sbLtyRevenue ;
				}
				else if (ocLoyaltyRevenue != null && ocLoyaltyRevenue != 0){
					totalLoyaltyRevenue = ocLoyaltyRevenue;
				}*/
				
				if(totalLoyaltyRevenue != 0.0) {
					Double tempDbl =  calcPercentageDifference(Double.parseDouble(loyaltyRevenueLblId.getValue()), totalLoyaltyRevenue);
					if(tempDbl != null && tempDbl != 0.0) {
						loyaltyRevenueCompDivId.setVisible(true);
						setPercentageChangeByDivId(loyaltyRevenueCompDivId, loyaltyRevenueCompLblId, tempDbl);
					}
				}
				
				//Double totalRedemption = 0.0;
				//totalRedemption = sbLtyRevenue == null ? 0.0 : sbLtyRevenue;
				
				//GiftCard Revenue
				Double totalGiftCardRevenue = 0.0;
				//calling findTotalGiftCardRevenue which calculates total revenue for Sparkbase & OC loyalty based on  gift.
				totalGiftCardRevenue = findTotalGiftCardRevenue(userId, pastStartCalStr, pastEndCalStr);

				if(totalGiftCardRevenue != 0.0) {
					Double tempDbl =  calcPercentageDifference(Double.parseDouble(giftcardRevenueLblId.getValue()), totalGiftCardRevenue);
					if(tempDbl != null && tempDbl != 0.0) {
						giftcardRevenueCompDivId.setVisible(true);
						setPercentageChangeByDivId(giftcardRevenueCompDivId, giftcardRevenueCompLblId, tempDbl);
					}
				}
				//No.of Reedemption
				//calling findTotalRedemption which calculates total redemption for both sparkbase & ocloyalty 
				Long totalRedemption = findTotalRedemption(userId, pastStartCalStr, pastEndCalStr);
				//				
				logger.info("numOfReedmptionLong .."+totalRedemption);
				logger.info("noofRedemptionsLblId.getValue() .."+noofRedemptionsLblId.getValue());
				if(totalRedemption !=0 && totalRedemption != null && noofRedemptionsLblId.getValue() != null
						&& !(noofRedemptionsLblId.getValue().trim().equals(""))) {
					Double tempDbl =  calcPercentageDifference(Long.parseLong(noofRedemptionsLblId.getValue()), totalRedemption);
					if(tempDbl != null && tempDbl != 0.0) {
						noOfRedemptionsCompDivId.setVisible(true);
						setPercentageChangeByDivId(noOfRedemptionsCompDivId, noOfRedemptionsCompLblId, tempDbl);
					}
				}
				
				//Points Issued in Loyalty
				Long ocPtsIssued = findPointsIssuedOC(userId, pastStartCalStr, pastEndCalStr)	;
				if(ocPtsIssued!=0 && ocPtsIssued != null && noofPointIssuancesLblId.getValue() != null
						&& !(noofPointIssuancesLblId.getValue().trim().equals(""))) {
					Double tempDbl =  calcPercentageDifference(Long.parseLong(noofPointIssuancesLblId.getValue()), ocPtsIssued);
					if(tempDbl != null && tempDbl != 0.0) {
						noOfPointIssuancesCompDivId.setVisible(true);
						setPercentageChangeByDivId(noOfPointIssuancesCompDivId, noOfPointIssuancesCompLblId, tempDbl);
					}
				}
				
				
				//Amount Issued in Loyalty
				Double ocAmtIssued = findAmountIssuedOC(userId, pastStartCalStr, pastEndCalStr)	;
				 logger.debug("Past Amount Issued  is..."+ocAmtIssued);
	                if(ocAmtIssued!=0.0 && ocAmtIssued != null && noofIssuancesLblId.getValue() != null
						&& !(noofIssuancesLblId.getValue().trim().equals(""))) {
					Double tempDbl =  calcPercentageDifference(Double.parseDouble(noofIssuancesLblId.getValue()), ocAmtIssued);
					if(tempDbl != null && tempDbl != 0.0) {
						noOfIssuancesCompDivId.setVisible(true);
						setPercentageChangeByDivId(noOfIssuancesCompDivId, noOfIssuancesCompLblId, tempDbl);
					}
				}
				
				//Points Redeemed 
				LoyaltyTransactionChildDao loyaltyTransactionChildDao1 = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				Long ocPtsRedeemed = loyaltyTransactionChildDao1.findPointsRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,pastStartCalStr, pastEndCalStr);
				if(ocPtsRedeemed!=0 && ocPtsRedeemed != null && pointsReedemedLblId.getValue() != null
						&& !(pointsReedemedLblId.getValue().trim().equals(""))) {
					Double tempDbl =  calcPercentageDifference(Long.parseLong(pointsReedemedLblId.getValue()), ocPtsRedeemed);
					if(tempDbl != null && tempDbl != 0.0) {
						pointsReedemedCompDivId.setVisible(true);
						setPercentageChangeByDivId(pointsReedemedCompDivId, pointsReedemedCompLblId, tempDbl);
					}
				}
				
				
				//Amount Redeemed 
				logger.info("Amount Redeemed :: "+Double.parseDouble(amountReedemedLblId.getValue()));
				//LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				Double ocAmtRedeemed = findAmountRedeemedOC(userId,pastStartCalStr, pastEndCalStr);
				if(ocAmtRedeemed!=0.0 && ocAmtRedeemed != null && amountReedemedLblId.getValue() != null
						&& !(amountReedemedLblId.getValue().trim().equals(""))) {
					Double tempDbl =  calcPercentageDifference(Double.parseDouble(amountReedemedLblId.getValue()), ocAmtRedeemed);
					if(tempDbl != null && tempDbl != 0.0) {
						amountReedemedCompDivId.setVisible(true);
						setPercentageChangeByDivId(amountReedemedCompDivId, amountReedemedCompLblId, tempDbl);
					}
				}
				
				//Gift-Card Issuance
				LoyaltyTransactionChildDao loyaltyTransactionChildDao2 = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				Long totalGftIssuance = loyaltyTransactionChildDao2.findTotGiftIssuance(userId,pastStartCalStr,pastEndCalStr);
				if(totalGftIssuance != null && giftCardISSLblId.getValue() != null
						&& !(giftCardISSLblId.getValue().trim().equals(""))) {
					Double tempDb =  calcPercentageDifference(Long.parseLong(giftCardISSLblId.getValue()), totalGftIssuance);
					if(tempDb != null && tempDb != 0.0) {
						giftCardIssCompDivId.setVisible(true);
						setPercentageChangeByDivId(giftCardIssCompDivId, giftCardIssCompLblId, tempDb);
					}
				}

			}else {
				compareToDivId.setVisible(false);
			}
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}

		}//onCheck$compToPastChkBxId
		
		
		private Double calcPercentageDifference(Double newNum,Double orgNum) {

			logger.info("Current : "+ newNum + " PAST VAL :" + newNum);
			if(orgNum == 0.0) {

				return null;
			}
			return (newNum - orgNum)* 100 / orgNum; 
		}
		
		private Double calcPercentageDifference(int newNum,int orgNum) {

			logger.info("Current : "+ newNum + " PAST VAL :" + orgNum);
			if(orgNum == 0) {

				return null;
			}
			/*Double temp = (newNum - orgNum) / Double.parseDouble(Integer.toString(orgNum));
			logger.info("temp"+ temp);
			return temp * 100; */
			Double temp = (newNum - orgNum) *100/ Double.parseDouble(Integer.toString(orgNum));
			logger.info("temp"+ temp);
			return temp ; 
		}
		
		
		private Double calcPercentageDifference(Long newNum,Long orgNum) {

			logger.info("Current : "+ newNum + " PAST VAL :" + orgNum);
			if(orgNum == 0) {

				return null;
			}
			Double temp = (newNum - orgNum) * 100/ Double.parseDouble(Long.toString(orgNum));
			logger.info("temp"+ temp);
			return temp ; 
			//			return temp * 100; 
		}
		
		
		private void  setPercentageChangeByDivId(Div div,Label divLbl,Double val) {

			logger.info("DOUBLE VAL : "+ val);
			logger.info("ROUNDED : " + Math.round(val));

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
	
		/**
		 * This method calculates total Loyalty card shoppers for Sparkbase & OC loyalty.
		 * @param userId
		 * @param startCalStr
		 * @param endCalStr
		 * @return
		 */

		private Long findTotalLoyaltyOptins(Long userId, String startCalStr,String endCalStr) {
			logger.info("entered findTotalLoyaltyOptins");
			Long totalLoyltyOptins = 0L;
			//SB Shoppers
			Long sbLtyOptins = contactsLoyaltyDao.findTotalSBLoyaltyOptins(userId,OCConstants.LOYALTY_SERVICE_TYPE_SB,startCalStr,endCalStr);
			//OC shoppers
			Long ocLtyOptins =  contactsLoyaltyDao.findTotalOCLoyaltyOptins(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,startCalStr,endCalStr);

			logger.info("sbLtyOptins ::"+sbLtyOptins+" \tocLtyOptins ::"+ocLtyOptins);
			if((sbLtyOptins != null && sbLtyOptins != 0) && (ocLtyOptins != null && ocLtyOptins != 0)){
				totalLoyltyOptins = sbLtyOptins + ocLtyOptins;
			}
			else if(sbLtyOptins != null && sbLtyOptins != 0){
				totalLoyltyOptins = sbLtyOptins;
			}
			else if(ocLtyOptins != null && ocLtyOptins != 0){
				totalLoyltyOptins = ocLtyOptins;

			}
			logger.info("completed findTotalLoyaltyOptins");
			return totalLoyltyOptins;
		}//findTotalLoyaltyCardShoppers
		
		
		private Long findTotalLoyaltyCardShoppers(Long userId, String startCalStr,	String endCalStr) {
			logger.info("entered findTotalLoyaltyCardShoppers");
			Long totalLtyShoppers = 0L;
			//SB Shoppers
			Long sbLtyShoppers = retailProSalesDao.findTotalCountByServiceType(userId,OCConstants.LOYALTY_SERVICE_TYPE_SB,
																			startCalStr, endCalStr);
			//OC shoppers
			Long ocLtyShoppers =  retailProSalesDao.findTotalCountByServiceTypeAndRewardFlag(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,startCalStr,endCalStr);

			logger.info("sbLtyShoppers ::"+sbLtyShoppers+" \tocLtyShoppers ::"+ocLtyShoppers);
			if((sbLtyShoppers != null && sbLtyShoppers != 0) && (ocLtyShoppers != null && ocLtyShoppers != 0)){
				totalLtyShoppers = sbLtyShoppers + ocLtyShoppers;
			}
			else if(sbLtyShoppers != null && sbLtyShoppers != 0){
				totalLtyShoppers = sbLtyShoppers;
			}
			else if(ocLtyShoppers != null && ocLtyShoppers != 0){
				totalLtyShoppers = ocLtyShoppers;

			}
			logger.info("completed findTotalLoyaltyCardShoppers");
			return totalLtyShoppers;
		}

		/**
		 * This method calculates total gift card shoppers for Sparkbase & OC loyalty.
		 * @param userId
		 * @param startCalStr
		 * @param endCalStr
		 * @return totalGiftCardShoppers
		 */
		/*private Long findTotalGiftCardShoppers(Long userId, String startCalStr, String endCalStr) {
			logger.info("entered findTotalGiftCardShoppers");
			Long totalGiftCardShoppers = 0L;
			Long sbGiftCardShoppers = retailProSalesDao.findTotalCountByCardType(userId, "'"+Constants.CARD_TYPE_GIFT_CARD+"'" , startCalStr, endCalStr);
			Long ocGiftCardShoppers = retailProSalesDao.findTotalCountByServiceTypeAndRewardFlag(userId, OCConstants.LOYALTY_SERVICE_TYPE_OC, "gift", startCalStr, endCalStr);

			logger.info("sbGiftCardShoppers ::"+sbGiftCardShoppers+" \tocGiftCardShoppers ::"+ocGiftCardShoppers);
			if((sbGiftCardShoppers != null && sbGiftCardShoppers !=0) && (ocGiftCardShoppers != null && ocGiftCardShoppers !=0)){
				totalGiftCardShoppers = sbGiftCardShoppers + ocGiftCardShoppers;
			}
			else if(sbGiftCardShoppers != null && sbGiftCardShoppers !=0){
				totalGiftCardShoppers = sbGiftCardShoppers ;
			}
			else if(ocGiftCardShoppers != null && ocGiftCardShoppers !=0){
				totalGiftCardShoppers =  ocGiftCardShoppers;
			}
			logger.info("completed findTotalGiftCardShoppers");
			return totalGiftCardShoppers;
		}//findTotalGiftCardShoppers
*/
		/**
		 * This method find's the total revenue for Sparkbase & OC loyalty based on  loyalty & Gift to Loyalty.
		 * @param userId
		 * @param startCalStr
		 * @param endCalStr
		 * @return totalLtyRevenue
		 */
		private Double findOcTotalRevenue(Long userId, String startCalStr,String endCalStr) {
			logger.info("entered findOcTotalRevenue");
			Double ocLtyRevenue = retailProSalesDao.findTotalRevenueByServiceTypeAndRewardFlag(userId,OCConstants.LOYALTY_SERVICE_TYPE_OC,"loyaltyGift",startCalStr,endCalStr);
			logger.info("completed findOcTotalRevenue");
			return ocLtyRevenue;
		}//findTotalRevenue

		/**
		 * This method find's the total revenue for gift Sparkbase & OC loyalty based on  loyalty.
		 * @param userId
		 * @param startCalStr
		 * @param endCalStr
		 * @return totalGiftCardRevenue
		 */
		private Double findTotalGiftCardRevenue(Long userId, String startCalStr,String endCalStr) {
			logger.info("entered findTotalGiftCardRevenue");
			Double totalGiftCardRevenue = 0.0;
			try {
			/*totalGiftCardRevenue =  retailProSalesDao.findTotalRevenueByCardType(userId, "'"+Constants.CARD_TYPE_GIFT_CARD+"'" ,
																					startCalStr, endCalStr);*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totalGiftCardRevenue += loyaltyTransactionChildDao.findTotalGiftRevenue(userId,startCalStr,endCalStr);

			logger.info("completed findTotalGiftCardRevenue");
			}
			catch(Exception e) {
				logger.error("Exception ::",e);
			}
			return totalGiftCardRevenue;
		}//findTotalGiftCardRevenue

		/**
		 * This method finds the total redemption for both sparkbase & ocloyalty 
		 * @param userId
		 * @param startCalStr
		 * @param endCalStr
		 * @return totalRedemption
		 */
		private Long findTotalRedemption(Long userId, String startCalStr,String endCalStr) {
			logger.info("entered findTotalRedemption");
			Long totalRedemption = 0L;
			//Long  sbNumberOfReedmption = retailProSalesDao.findReedmptionByTypes(userId, startCalStr, endCalStr);
			totalRedemption =  retailProSalesDao.findRedemptionForOcType(userId,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,startCalStr, endCalStr);

			/*if((sbNumberOfReedmption != null && sbNumberOfReedmption != 0) && (ocNumberOfReedmption != null && ocNumberOfReedmption != 0)){
				totalRedemption = sbNumberOfReedmption + ocNumberOfReedmption;
			}
			else if(sbNumberOfReedmption != null && sbNumberOfReedmption != 0){
				totalRedemption = sbNumberOfReedmption ;
			}
			else if(ocNumberOfReedmption != null && ocNumberOfReedmption != 0){
				totalRedemption = ocNumberOfReedmption;
			}*/
			logger.info("numOfReedmptionLong is .."+totalRedemption);
			/*if( totalRedemption !=0){
				noofRedemptionsLblId.setValue(""+totalRedemption);
			}else {
				noofRedemptionsLblId.setValue("0");
			}*/
			logger.info("completed findTotalRedemption");
			return totalRedemption;
		}//findTotalRedemption
		
		
			public static void main(String args[])
			{
			int num1=5;
			float num2=0f;
			double num3=num1/num2;
			System.out.println(num3); //Guess,What is out put??
			}
			
	
}
