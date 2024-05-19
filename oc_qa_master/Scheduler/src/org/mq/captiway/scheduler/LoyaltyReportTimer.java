package org.mq.captiway.scheduler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.LoyaltyFraudAlert;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.UserEmailAlert;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.UserEmailAlertDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.data.dao.LoyaltyFraudAlertDao;
import org.mq.optculture.data.dao.LoyaltyFraudAlertDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.model.DR.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyReportTimer extends TimerTask {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private UsersDao usersDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private UserEmailAlertDao userEmailAlertDao;
	private LoyaltyTransactionChildDao loyaltyTransactionChildDao;
	private RetailProSalesDao retailProSalesDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyFraudAlertDao loyaltyFraudAlertDao;
	private LoyaltyFraudAlertDaoForDML loyaltyfrdAlertDaoForDML;
	private OrganizationStoresDao organizationStoresDao;
	private Calendar startDate, endDate;
	private User User;
	private Map<String, String> storeNameMap;

	@Override
	public void run() {
		try {
			ServiceLocator locator = ServiceLocator.getInstance();

			usersDao = (UsersDao) locator.getDAOByName("usersDao");
			organizationStoresDao = (OrganizationStoresDao) locator.getDAOByName("organizationStoresDao");
			
			emailQueueDao = (EmailQueueDao)locator.getDAOByName("emailQueueDao");
			emailQueueDaoForDML = (EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML");
			userEmailAlertDao = (UserEmailAlertDao)locator.getDAOByName("userEmailAlertDao");
			retailProSalesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName("retailProSalesDao");
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName("loyaltyProgramDao");
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance()
					.getDAOByName("loyaltyProgramTierDao");
			loyaltyFraudAlertDao = (LoyaltyFraudAlertDao) ServiceLocator.getInstance()
					.getDAOByName("loyaltyFraudAlertDao");
			loyaltyfrdAlertDaoForDML=(LoyaltyFraudAlertDaoForDML) locator.getDAOForDMLByName("loyaltyFraudAlertDaoForDML");
			//String type = OCConstants.LTY_SETTING_REPORT_TYPE;
			StringBuffer sb = null;
			StringBuffer strBuff = null;
			getFraudAlertTrxReport();
			List<Users> users = usersDao.getAllUsers();
			
			int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE));
			logger.info("LoyaltyReportTimer start=========");
			
			logger.info("Server timezone for alrts email"+serverTimeZoneValInt);

			for(Users user : users) {
				try{

					storeNameMap = new HashMap<String, String>();
				
					logger.info("User is =="+user.getUserName());
					List<UserEmailAlert> userEmailAlertsObjList = userEmailAlertDao.findListByUserId(user.getUserId());
					if(userEmailAlertsObjList == null || userEmailAlertsObjList.size() == 0 )continue;
					boolean isDailyDone=false;
					boolean isWeeklyDone=false;

					List<LoyaltyProgram> loyaltyProgramsList = loyaltyProgramDao.findListByUserIdAndStatus(user.getUserId());

					if(loyaltyProgramsList != null && loyaltyProgramsList.size() >0){
						int timezoneDiffrenceMinutesInt = 0;
						String timezoneDiffrenceMinutes = user.getClientTimeZone();
					
						logger.info("client timezone for alrts email"+timezoneDiffrenceMinutes);
						
						//	TimeZone clientTimezone = TimeZone.getTimeZone("");
						if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
						timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt - serverTimeZoneValInt;
					
						
						logger.info("client timezone for alrts email after difference"+timezoneDiffrenceMinutesInt);

						for (LoyaltyProgram loyaltyProgramObj : loyaltyProgramsList) {

							for (UserEmailAlert userEmailAlertObj : userEmailAlertsObjList) {
								logger.info("userEmailAlert obj is ====="+ userEmailAlertObj.getUserId());

								Calendar calLastSentOn1 = userEmailAlertObj.getLastSentOn();
								
								Calendar calLastSentOn = (calLastSentOn1 != null ? (Calendar) calLastSentOn1.clone() :null);
								if(calLastSentOn != null)
								calLastSentOn.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								
								Calendar newCal = Calendar.getInstance();
								newCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								
								if(userEmailAlertObj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY) && userEmailAlertObj.isEnabled() &&
										(userEmailAlertObj.getLastSentOn() == null || !MyCalendar.calendarToString(calLastSentOn, 
												MyCalendar.FORMAT_DATEONLY).equalsIgnoreCase(MyCalendar.calendarToString(newCal, MyCalendar.FORMAT_DATEONLY))) ){
									Calendar currCal = Calendar.getInstance();
									currCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);

									Calendar cal1 = Calendar.getInstance();
									cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									//cal1.setTimeInMillis(currCal.getTimeInMillis());
									cal1.set(Calendar.HOUR_OF_DAY, 0);
									cal1.set(Calendar.MINUTE, 0);
									cal1.set(Calendar.SECOND, 0);
									logger.info("Date is --"+MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									cal1.set(Calendar.HOUR_OF_DAY, Integer.valueOf(userEmailAlertObj.getTriggerAt()).intValue()+1);
									logger.info("Date is cal1--"+MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									logger.info("Date is currCal--"+MyCalendar.calendarToString(currCal, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									if(!currCal.before(cal1)){
										logger.info("inside condition if----");
										currCal.add(currCal.DAY_OF_MONTH, -1);	
										//Date today = currCal.getTime();
										Calendar cal = Calendar.getInstance();
										cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
										cal.set(Calendar.HOUR_OF_DAY, 0);
										cal.set(Calendar.MINUTE, 0);
										cal.set(Calendar.SECOND, 0);
										cal.add(currCal.DAY_OF_MONTH, -1);
										String fromDate =MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);
										cal.add(Calendar.HOUR_OF_DAY, 23);
										cal.add(Calendar.MINUTE,59);
										cal.add(Calendar.SECOND, 59);
										String toDate =MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);
										String htmlContent = PropertyUtil.getPropertyValueFromDB("dailyLtyEmailRportTemplate");
										logger.info("---before kpiReportHtml---");
										sb = prepareKpiReportHtml(userEmailAlertObj.getUserId(),fromDate,toDate,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<dailyLty_KPI_report_tables>", (sb == null ? "" : sb.toString()));

										sb = null;
										logger.info("---before TransactionSummaryReportHtml---");
										sb = prepareTransactionSummaryReportHtml(userEmailAlertObj.getUserId(),fromDate, toDate,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<dailyLty_TransactionSummary_report_tables>", (sb == null ? "" : sb.toString()));

										sb = null;
										logger.info("---before KPIsDetailedReportHtml---");
										sb = prepareKPIsDetailedReportHtml(userEmailAlertObj.getUserId(),fromDate, toDate,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<dailyLty_TierKPI_Detailed_report_tables>", (sb == null ? "" : sb.toString()));

										sb = null;
										logger.info("---before StoreKPIsDetailedReportHtml---");
										logger.info("***Starting StoreKPIsDetailedReportHtml***");
										sb = prepareStoreKPIsDetailedReportHtml(userEmailAlertObj.getUserId(),fromDate, toDate,loyaltyProgramObj.getProgramId());
										logger.info("completed prepareStoreKPIsDetailedReportHtml sb value is"+sb);
										logger.info("-----------prepareStoreKPIsDetailedReportHtml------"+(sb == null ? "null" : sb.toString()));
										htmlContent = htmlContent.replace("<dailyLty_StoreKPI_detailed_report_tables>", (sb == null ? "" : sb.toString()));
										logger.info("htmlContent value for StoreKPIsDetailedReportHtml is"+htmlContent);
									
										sb = null;
										logger.info("---before StoreTransactionDetailedReportHtml---");
										logger.info("***Starting StoreTransaction_detailed_report_tables***");
										sb = prepareStoreTransactionDetailedReportHtml(userEmailAlertObj.getUserId(),fromDate, toDate,loyaltyProgramObj.getProgramId());
										logger.info("completed StoreTransactionDetailedReportHtml sb value is"+sb);
										logger.info("----------prepare StoreTransactionDetailedReportHtml-------"+(sb == null ? "null" : sb.toString()));
										htmlContent = htmlContent.replace("<dailyLty_StoreTransaction_detailed_report_tables>", (sb == null ? "" : sb.toString()));
										logger.info("htmlContent value for StoreTransactionDetailedReportHtml  is"+htmlContent);

										sendAlertDaily(loyaltyProgramObj, htmlContent,currCal,userEmailAlertObj);
										isDailyDone = true;
										logger.info("-----sending daily----");
									}
								}


								if(userEmailAlertObj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK) && userEmailAlertObj.isEnabled() && 
										(userEmailAlertObj.getLastSentOn() == null || !MyCalendar.calendarToString(calLastSentOn, 
												MyCalendar.FORMAT_DATEONLY).equalsIgnoreCase(MyCalendar.calendarToString(newCal, MyCalendar.FORMAT_DATEONLY)))){
									logger.info("----------Weekly Report---------");
									Calendar currCal1 = Calendar.getInstance();
									currCal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);

									Calendar from = Calendar.getInstance();
									logger.info("---from date---"+MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									from.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									//from.setTimeInMillis(currCal1.getTimeInMillis());
									from.add(Calendar.DAY_OF_MONTH, -7);
									from.set(Calendar.HOUR_OF_DAY,0);
									from.set(Calendar.MINUTE,0);
									from.set(Calendar.SECOND,0);
									logger.info("---from date after add days---"+MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));

									Calendar till = Calendar.getInstance();
									logger.info("---till date---"+MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									till.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									//till.setTimeInMillis(currCal1.getTimeInMillis());
									till.add(Calendar.DAY_OF_MONTH, -1);
									till.set(Calendar.HOUR_OF_DAY,23);
									till.set(Calendar.MINUTE,59);
									till.set(Calendar.SECOND,59);

									logger.info("---till date after add days---"+MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									//String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
									//String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

									String str[] = userEmailAlertObj.getTriggerAt().split(Constants.ADDR_COL_DELIMETER);
									//from.add(Calendar.HOUR_OF_DAY, (Integer.valueOf(str[0]).intValue())+1);

									Calendar cal1 = Calendar.getInstance();
									cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									//cal1.setTimeInMillis(currCal1.getTimeInMillis());
									cal1.set(Calendar.HOUR_OF_DAY, 0);
									cal1.set(Calendar.MINUTE, 0);
									cal1.set(Calendar.SECOND, 0);

									logger.info("Weekly Date is cal1--"+MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									logger.info("Weekly Date is currCal1--"+MyCalendar.calendarToString(currCal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									cal1.set(Calendar.HOUR_OF_DAY, Integer.valueOf(str[0]).intValue()+1);

									
									logger.info("currCal1.get(Calendar.DAY_OF_WEEK)  --"+(currCal1.get(Calendar.DAY_OF_WEEK))+" >>> Integer.valueOf(str[1]) "+(Integer.valueOf(str[1]).intValue()));
									if((currCal1.get(Calendar.DAY_OF_WEEK)-1)==(Integer.valueOf(str[1]).intValue()) && !currCal1.before(cal1)){

										logger.info("----inside weekly if condition----");

										String htmlContent = PropertyUtil.getPropertyValueFromDB("weeklyLtyEmailReportTemplate");

										strBuff = weeklyKpiSummaryHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_KPI_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										strBuff = null;
										strBuff = weeklyTransactionSummaryReportHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_TransactionSummary_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										strBuff = null;
										strBuff = weeklyTierKpiDetailedReportHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_TierKPI_Detailed_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										strBuff = null;
										strBuff = weeklyStoreKpiDetailedReportHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_StoreKPI_detailed_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										strBuff = null;
										strBuff = weeklyStoreTranAmtReportHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_StoreTransacted_amount_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										strBuff = null;
										strBuff = weeklyStoreTransactionReportHtml(userEmailAlertObj.getUserId(),from,till,loyaltyProgramObj.getProgramId());
										htmlContent = htmlContent.replace("<weeklyLty_StoreTransaction_store_report_tables>", (strBuff == null ? "" : strBuff.toString()));

										sendAlertWeekly(loyaltyProgramObj, htmlContent,from,till,userEmailAlertObj);
										isWeeklyDone = true;
										logger.info("-----sending weekly----");
									}

								}

								/*if(userEmailAlertObj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_MONTH) && userEmailAlertObj.isEnabled()){

					}*/
							}

						}
						for(UserEmailAlert userEmailAlertObj:userEmailAlertsObjList){
							if((userEmailAlertObj.getFrequency().equalsIgnoreCase(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)&& isDailyDone )||
									(userEmailAlertObj.getFrequency().equalsIgnoreCase(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)&& isWeeklyDone)){
								userEmailAlertObj.setLastSentOn(Calendar.getInstance());
								emailQueueDaoForDML.saveOrUpdate(userEmailAlertObj);
							}
						}
					}
				}catch(Exception e){
					logger.info("Exception",e);
					e.printStackTrace();
				}
			}
		//	getFraudAlertTrxReport();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Exception", e);
		}
		
	}
	public void getFraudAlertTrxReport(){
		logger.info("inside getFraudAlertTrxReport start");
		List<Long> userIdlist=loyaltyFraudAlertDao.getAllusersId();
		
		if(userIdlist==null)
			return;
		logger.info("user List id"+userIdlist);
		for (Long userId : userIdlist) {
			Users user = usersDao.findByUserId(userId);
			try {
				logger.info("User is ==" + user.getUserName()+" userID"+userId);
				int timezoneDiffrenceMinutesInt = 0;
				int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE));
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt - serverTimeZoneValInt;
			    logger.info("time zone diff "+timezoneDiffrenceMinutesInt);
				List<LoyaltyFraudAlert> fraudAlertTrxList = loyaltyFraudAlertDao.findSentEmailRule(userId,true);
				if (fraudAlertTrxList == null || fraudAlertTrxList.size() == 0)
					continue;
				for (LoyaltyFraudAlert ltyFrdTrxobj : fraudAlertTrxList) {
						LoyaltyFraudAlert loyaltyFraudAlert = loyaltyFraudAlertDao.getFraudAlertRuleByName(ltyFrdTrxobj.getCreatedByUserId(),ltyFrdTrxobj.getRuleName());
						 logger.info("==========inside getFraudAlertTrxReport for loop");
						if (loyaltyFraudAlert != null) {
							String trxRule = null;
							String dateRule = null;
							if (loyaltyFraudAlert != null) {
								trxRule = loyaltyFraudAlert.getTrxRule();
								dateRule = loyaltyFraudAlert.getDateRule();
							}
						Calendar calLastSentOn1 = ltyFrdTrxobj.getLastSentOn();

						Calendar calLastSentOn = (calLastSentOn1 != null ? (Calendar) calLastSentOn1.clone() : null);
						if (calLastSentOn != null)
							calLastSentOn.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);

						Calendar newCal = Calendar.getInstance();
						newCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
						if (ltyFrdTrxobj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)
								&& ltyFrdTrxobj.isEnabled()
								&& (ltyFrdTrxobj.getLastSentOn() == null || !MyCalendar
										.calendarToString(calLastSentOn, MyCalendar.FORMAT_DATEONLY).equalsIgnoreCase(
												MyCalendar.calendarToString(newCal, MyCalendar.FORMAT_DATEONLY)))) {
							logger.info("=========getFraudAlertTrxReport daily report ==============");
							Calendar currCal = Calendar.getInstance();
							currCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							Calendar cal1 = Calendar.getInstance();
							cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							cal1.set(Calendar.HOUR_OF_DAY, 0);
							cal1.set(Calendar.MINUTE, 0);
							cal1.set(Calendar.SECOND, 0);
							logger.info("getFraudAlertTrxReport Date is --"
									+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							cal1.set(Calendar.HOUR_OF_DAY, Integer.valueOf(ltyFrdTrxobj.getTriggerAt()).intValue());
							logger.info("getFraudAlertTrxReport Date is cal1--"
									+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							logger.info("getFraudAlertTrxReport Date is currCal--"
									+ MyCalendar.calendarToString(currCal, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							if (!currCal.before(cal1)) {
								logger.info("getFraudAlertTrxReport inside condition if----");
								currCal.add(currCal.DAY_OF_MONTH, -1);
								// Date today = currCal.getTime();
								Calendar cal = Calendar.getInstance();
								cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								cal.set(Calendar.HOUR_OF_DAY, 0);
								cal.set(Calendar.MINUTE, 0);
								cal.set(Calendar.SECOND, 0);
								cal.add(currCal.DAY_OF_MONTH, -1);
								String fromDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);
								cal.add(Calendar.HOUR_OF_DAY, 23);
								cal.add(Calendar.MINUTE, 59);
								cal.add(Calendar.SECOND, 59);
								String toDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);	
					             List<Map<String, Object>> list = loyaltyFraudAlertDao.getFraudAlertTrxByUserId(user.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule, user));
			                         if(list==null)
			                        	 continue;
			                        
				                   String htmlContent = PropertyUtil.getPropertyValueFromDB("loyaltyFraudAlertsTemplate");
				              if(htmlContent ==null)
				              return;
					                      for(Map map:list){
					                    	  htmlContent = htmlContent.replace("[user_first_name]", user.getFirstName());
					              		     htmlContent=htmlContent.replace("[alert_rule_name]",ltyFrdTrxobj.getRuleName());
					              			htmlContent = htmlContent.replace("[daily_Weekly_date]",
					            					MyCalendar.calendarToString(cal, MyCalendar.FORMAT_FULLMONTHDATEONLY));
					              			htmlContent = htmlContent.replace("[rule_description]", getTrxRuleForDisplay(ltyFrdTrxobj.getTrxRule()));
					              			 htmlContent = htmlContent.replace("[date_range]", getDateRuleForDisplay(ltyFrdTrxobj.getDateRule()));
					              		  htmlContent = htmlContent.replace("[NoOfCard]", ""+( map.get("no_of_cards")!=null?map.get("no_of_cards"):"0"));
										  htmlContent = htmlContent.replace("[TotalTrx]", ""+( map.get("total_trx")!=null? map.get("total_trx"):"0"));
										  htmlContent = htmlContent.replace("[TotalIssTrx]", ""+( map.get("total_issuance")!=null?map.get("total_issuance"):"0"));
										  htmlContent = htmlContent.replace("[TotalRedTrx]", ""+ (map.get("total_redemption")!=null?map.get("total_redemption"):"0"));
										  htmlContent = htmlContent.replace("[TotalIssAmt]", ""+ (map.get("total_issuance_amt")!=null?roundOffTo2DecPlaces((Double)map.get("total_issuance_amt")):"0.00"));
									      htmlContent = htmlContent.replace("[TotalRedAmt]",""+(map.get("total_redemption_amt")!=null? roundOffTo2DecPlaces((Double)map.get("total_redemption_amt")):"0.00"));
					                      }
				                            		
				              sendFraudAlertDaily(htmlContent,cal,ltyFrdTrxobj);
				              logger.info("=======getFraudAlertTrxReport daily Report  done==============");
							}
							
							
				        }
						if (ltyFrdTrxobj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)
								&& ltyFrdTrxobj.isEnabled()
								&& (ltyFrdTrxobj.getLastSentOn() == null || !MyCalendar
										.calendarToString(calLastSentOn, MyCalendar.FORMAT_DATEONLY).equalsIgnoreCase(
												MyCalendar.calendarToString(newCal, MyCalendar.FORMAT_DATEONLY)))) {
							logger.info("----------getFraudAlertTrxReport Weekly Report---------");
							Calendar currCal1 = Calendar.getInstance();
							currCal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							Calendar from = Calendar.getInstance();
							logger.info("---getFraudAlertTrxReport from date---"
									+ MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							from.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							// from.setTimeInMillis(currCal1.getTimeInMillis());
							from.add(Calendar.DAY_OF_MONTH, -7);
							from.set(Calendar.HOUR_OF_DAY, 0);
							from.set(Calendar.MINUTE, 0);
							from.set(Calendar.SECOND, 0);
							logger.info("---getFraudAlertTrxReport from date after add days---"
									+ MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));

							Calendar till = Calendar.getInstance();
							logger.info("---getFraudAlertTrxReport till date---"
									+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							till.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							// till.setTimeInMillis(currCal1.getTimeInMillis());
							till.add(Calendar.DAY_OF_MONTH, -1);
							till.set(Calendar.HOUR_OF_DAY, 23);
							till.set(Calendar.MINUTE, 59);
							till.set(Calendar.SECOND, 59);

							logger.info("---getFraudAlertTrxReport till date after add days---"
									+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
						
							String str[] = ltyFrdTrxobj.getTriggerAt().split(Constants.ADDR_COL_DELIMETER);
							
							Calendar cal1 = Calendar.getInstance();
							cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
							cal1.set(Calendar.HOUR_OF_DAY, 0);
							cal1.set(Calendar.MINUTE, 0);
							cal1.set(Calendar.SECOND, 0);
							// cal1.setTimeInMillis(currCal1.getTimeInMillis());

							logger.info("getFraudAlertTrxReport Weekly Date is cal1--"
									+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							logger.info("getFraudAlertTrxReport Weekly Date is currCal1--"
									+ MyCalendar.calendarToString(currCal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
							cal1.set(Calendar.HOUR_OF_DAY, Integer.valueOf(str[0]).intValue());
                            logger.info("getFraudAlertTrxReport Weekly cal1 "+cal1);
							logger.info("currCal1.get(Calendar.DAY_OF_WEEK)  --" + (currCal1.get(Calendar.DAY_OF_WEEK))
									+ " >>> Integer.valueOf(str[1]) " + (Integer.valueOf(str[1]).intValue()));
							//Calendar currCal1 = Calendar.getInstance();
							if ((currCal1.get(Calendar.DAY_OF_WEEK) - 1) == (Integer.valueOf(str[1]).intValue())
									&& !currCal1.before(cal1)) {
								logger.info("=============inside weekly if condition ");
				         List<Map<String, Object>> list = loyaltyFraudAlertDao.getFraudAlertTrxByUserId(user.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule, user));
							if(list==null)
								continue;
	
		                   String htmlContent = PropertyUtil .getPropertyValueFromDB("loyaltyFraudAlertsTemplate");
		                   if(htmlContent==null)
		                	   return;
			                      for(Map map:list){
			                     	  htmlContent = htmlContent.replace("[user_first_name]", user.getFirstName());
				              		  htmlContent=htmlContent.replace("[alert_rule_name]",ltyFrdTrxobj.getRuleName());
				              			htmlContent = htmlContent.replace("[daily_Weekly_date]",
				            					" the week between "+MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATE_YEAR) + " and "
				            							+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATE_YEAR));
				              			htmlContent = htmlContent.replace("[rule_description]", getTrxRuleForDisplay(ltyFrdTrxobj.getTrxRule()));
				              			 htmlContent = htmlContent.replace("[date_range]", getDateRuleForDisplay(ltyFrdTrxobj.getDateRule()));
				              			 htmlContent = htmlContent.replace("[NoOfCard]", ""+( map.get("no_of_cards")!=null?map.get("no_of_cards"):"0"));
										  htmlContent = htmlContent.replace("[TotalTrx]", ""+( map.get("total_trx")!=null? map.get("total_trx"):"0"));
										  htmlContent = htmlContent.replace("[TotalIssTrx]", ""+( map.get("total_issuance")!=null?map.get("total_issuance"):"0"));
										  htmlContent = htmlContent.replace("[TotalRedTrx]", ""+ (map.get("total_redemption")!=null?map.get("total_redemption"):"0"));
										  htmlContent = htmlContent.replace("[TotalIssAmt]", ""+ (map.get("total_issuance_amt")!=null?roundOffTo2DecPlaces((Double)map.get("total_issuance_amt")):"0.00"));
									      htmlContent = htmlContent.replace("[TotalRedAmt]",""+(map.get("total_redemption_amt")!=null? roundOffTo2DecPlaces((Double)map.get("total_redemption_amt")):"0.00"));
									      }
		                        
		                   sendFraudAlertWeekly(htmlContent,from,till,ltyFrdTrxobj);
		              logger.info("=======getFraudAlertTrxReport weekly Report  done==============");
		                  
							}
							
							
						}
						if(ltyFrdTrxobj.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY+Constants.ADDR_COL_DELIMETER+OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)
								&& ltyFrdTrxobj.isEnabled()
								&& (ltyFrdTrxobj.getLastSentOn() == null || !MyCalendar
										.calendarToString(calLastSentOn, MyCalendar.FORMAT_DATEONLY).equalsIgnoreCase(
												MyCalendar.calendarToString(newCal, MyCalendar.FORMAT_DATEONLY)))){
							 logger.info("=======getFraudAlertTrxReport daily and weekly  Report inside==============");
								String strFqr=loyaltyFraudAlert.getFrequency();
								String strFqrArr[]=strFqr.split(Constants.ADDR_COL_DELIMETER);
								if(strFqrArr[0].equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)){
									 logger.info("=======getFraudAlertTrxReport daily  Report inside in dailyWeekly==============");
									Calendar currCal = Calendar.getInstance();
									currCal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);

									Calendar cal1 = Calendar.getInstance();
									cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									cal1.set(Calendar.HOUR_OF_DAY, 0);
									cal1.set(Calendar.MINUTE, 0);
									cal1.set(Calendar.SECOND, 0);
									logger.info("getFraudAlertTrxReport Date is --"
											+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									String str[] = ltyFrdTrxobj.getTriggerAt().split(Constants.ADDR_COL_DELIMETER);
									cal1.set(Calendar.HOUR_OF_DAY,Integer.valueOf(str[0]).intValue() );
									logger.info("getFraudAlertTrxReport Date is cal1--"
											+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									logger.info("getFraudAlertTrxReport Date is currCal--"
											+ MyCalendar.calendarToString(currCal, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									if (!currCal.before(cal1)) {
										logger.info("getFraudAlertTrxReport inside condition if----");
										currCal.add(currCal.DAY_OF_MONTH, -1);
										// Date today = currCal.getTime();
										Calendar cal = Calendar.getInstance();
										cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
										cal.set(Calendar.HOUR_OF_DAY, 0);
										cal.set(Calendar.MINUTE, 0);
										cal.set(Calendar.SECOND, 0);
										cal.add(currCal.DAY_OF_MONTH, -1);
										String fromDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);
										cal.add(Calendar.HOUR_OF_DAY, 23);
										cal.add(Calendar.MINUTE, 59);
										cal.add(Calendar.SECOND, 59);
										String toDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR);	
							             List<Map<String, Object>> list = loyaltyFraudAlertDao.getFraudAlertTrxByUserId(user.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule, user));
					                         if(list==null)
					                        	 continue;
					                        
						                   String htmlContent = PropertyUtil.getPropertyValueFromDB("loyaltyFraudAlertsTemplate");
						              if(htmlContent ==null)
						              return;
							                      for(Map map:list){
							                    	  htmlContent = htmlContent.replace("[user_first_name]", user.getFirstName());
							              		 htmlContent=htmlContent.replace("[alert_rule_name]",ltyFrdTrxobj.getRuleName());
							              			htmlContent = htmlContent.replace("[daily_Weekly_date]",
							            					MyCalendar.calendarToString(cal, MyCalendar.FORMAT_FULLMONTHDATEONLY));
							              			htmlContent = htmlContent.replace("[rule_description]", getTrxRuleForDisplay(ltyFrdTrxobj.getTrxRule()));
							              			 htmlContent = htmlContent.replace("[date_range]", getDateRuleForDisplay(ltyFrdTrxobj.getDateRule()));
							              			 htmlContent = htmlContent.replace("[NoOfCard]", ""+( map.get("no_of_cards")!=null?map.get("no_of_cards"):"0"));
													  htmlContent = htmlContent.replace("[TotalTrx]", ""+( map.get("total_trx")!=null? map.get("total_trx"):"0"));
													  htmlContent = htmlContent.replace("[TotalIssTrx]", ""+( map.get("total_issuance")!=null?map.get("total_issuance"):"0"));
													  htmlContent = htmlContent.replace("[TotalRedTrx]", ""+ (map.get("total_redemption")!=null?map.get("total_redemption"):"0"));
													  htmlContent = htmlContent.replace("[TotalIssAmt]", ""+ (map.get("total_issuance_amt")!=null?roundOffTo2DecPlaces((Double)map.get("total_issuance_amt")):"0.00"));
												      htmlContent = htmlContent.replace("[TotalRedAmt]",""+(map.get("total_redemption_amt")!=null? roundOffTo2DecPlaces((Double)map.get("total_redemption_amt")):"0.00"));
							                      }
						                            		
						              sendFraudAlertDaily(htmlContent,cal,ltyFrdTrxobj);
						              logger.info("=======getFraudAlertTrxReport daily  Report  done in daily Weekly==============");
								  }
								}
								if(strFqrArr[1].equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)){
									 logger.info("=======getFraudAlertTrxReport weekly  Report inside in dailyWeekly==============");
									Calendar currCal1 = Calendar.getInstance();
									currCal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									Calendar from = Calendar.getInstance();
									logger.info("---getFraudAlertTrxReport from date---"
											+ MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									from.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									// from.setTimeInMillis(currCal1.getTimeInMillis());
									from.add(Calendar.DAY_OF_MONTH, -7);
									from.set(Calendar.HOUR_OF_DAY, 0);
									from.set(Calendar.MINUTE, 0);
									from.set(Calendar.SECOND, 0);
									logger.info("---getFraudAlertTrxReport from date after add days---"
											+ MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));

									Calendar till = Calendar.getInstance();
									logger.info("---getFraudAlertTrxReport till date---"
											+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									till.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									// till.setTimeInMillis(currCal1.getTimeInMillis());
									till.add(Calendar.DAY_OF_MONTH, -1);
									till.set(Calendar.HOUR_OF_DAY, 23);
									till.set(Calendar.MINUTE, 59);
									till.set(Calendar.SECOND, 59);

									logger.info("---getFraudAlertTrxReport till date after add days---"
											+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
								
									String str[] = ltyFrdTrxobj.getTriggerAt().split(Constants.ADDR_COL_DELIMETER);
									
									Calendar cal1 = Calendar.getInstance();
									cal1.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
									cal1.set(Calendar.HOUR_OF_DAY, 0);
									cal1.set(Calendar.MINUTE, 0);
									cal1.set(Calendar.SECOND, 0);
									// cal1.setTimeInMillis(currCal1.getTimeInMillis());

									logger.info("getFraudAlertTrxReport Weekly Date is cal1--"
											+ MyCalendar.calendarToString(cal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									logger.info("getFraudAlertTrxReport Weekly Date is currCal1--"
											+ MyCalendar.calendarToString(currCal1, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER));
									cal1.set(Calendar.HOUR_OF_DAY, Integer.valueOf(str[0]).intValue());
		                            logger.info("getFraudAlertTrxReport Weekly cal1 "+cal1);
									logger.info("currCal1.get(Calendar.DAY_OF_WEEK)  --" + (currCal1.get(Calendar.DAY_OF_WEEK))
											+ " >>> Integer.valueOf(str[1]) " + (Integer.valueOf(str[1]).intValue()));
									//Calendar currCal1 = Calendar.getInstance();
									if ((currCal1.get(Calendar.DAY_OF_WEEK) - 1) == (Integer.valueOf(str[1]).intValue())
											&& !currCal1.before(cal1)) {
										logger.info("=============inside weekly if condition ");
						  List<Map<String, Object>> list = loyaltyFraudAlertDao.getFraudAlertTrxByUserId(user.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule, user));
									if(list==null)
										continue;
			
				                   String htmlContent = PropertyUtil .getPropertyValueFromDB("loyaltyFraudAlertsTemplate");
				                   if(htmlContent==null)
				                	   return;
					                      for(Map map:list){
					                     	  htmlContent = htmlContent.replace("[user_first_name]", user.getFirstName());
						              		  htmlContent=htmlContent.replace("[alert_rule_name]",ltyFrdTrxobj.getRuleName());
						              			htmlContent = htmlContent.replace("[daily_Weekly_date]",
						            					" the week between "+MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATE_YEAR) + " and "
						            							+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATE_YEAR));
						              			htmlContent = htmlContent.replace("[rule_description]", getTrxRuleForDisplay(ltyFrdTrxobj.getTrxRule()));
						              			 htmlContent = htmlContent.replace("[date_range]", getDateRuleForDisplay(ltyFrdTrxobj.getDateRule()));
						              			 htmlContent = htmlContent.replace("[NoOfCard]", ""+( map.get("no_of_cards")!=null?map.get("no_of_cards"):"0"));
												  htmlContent = htmlContent.replace("[TotalTrx]", ""+( map.get("total_trx")!=null? map.get("total_trx"):"0"));
												  htmlContent = htmlContent.replace("[TotalIssTrx]", ""+( map.get("total_issuance")!=null?map.get("total_issuance"):"0"));
												  htmlContent = htmlContent.replace("[TotalRedTrx]", ""+ (map.get("total_redemption")!=null?map.get("total_redemption"):"0"));
												  htmlContent = htmlContent.replace("[TotalIssAmt]", ""+ (map.get("total_issuance_amt")!=null?roundOffTo2DecPlaces((Double)map.get("total_issuance_amt")):"0.00"));
											      htmlContent = htmlContent.replace("[TotalRedAmt]",""+(map.get("total_redemption_amt")!=null? roundOffTo2DecPlaces((Double)map.get("total_redemption_amt")):"0.00"));
					                      }
				                        
				                   sendFraudAlertWeekly(htmlContent,from,till,ltyFrdTrxobj);
				                   logger.info("=======getFraudAlertTrxReport weekly  Report  done in daily Weekly==============");
								}
							}
						}
					}
				}
			
			}
			catch (Exception e) {
				logger.info("Exception", e);
				e.printStackTrace();
			}
		}
	}

	public LoyaltyTransactionChildDao getLoyaltyTransactionChildDao() {
		return loyaltyTransactionChildDao;
	}

	public void setLoyaltyTransactionChildDao(
			LoyaltyTransactionChildDao loyaltyTransactionChildDao) {
		this.loyaltyTransactionChildDao = loyaltyTransactionChildDao;
	}

	private StringBuffer prepareTransactionSummaryReportHtml(Long userId, String fromDate, String toDate, Long prgmId) {
		try{
			logger.info("====D2====");
			StringBuffer sb = new StringBuffer();

			sb.append("<br/><br/><b style=\"margin-left:20px;\">Transactions Summary</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrollment</th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Loyalty Issuance</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issuance</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redemption</th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Return</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credit</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability</th></tr>");
			sb.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">No. of Transactions</td>");
			int[] str = transactionSummaryNumOfTrnTableValues(userId,fromDate,toDate,prgmId);
			if(str != null && str.length > 0){
				for(int i = 0; i < str.length; i++){
					if(i == 6)sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+str[i]+"</td>");

				}
			}else {
				for(int i = 0; i < 7; i++){
					if(i == 6)sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
			}
			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Points</td>");
			Double[] str1 = transactionSummaryPointsTableValues(userId,fromDate,toDate,prgmId);
			if(str1 != null && str1.length > 0){
				for(int i = 0; i < str1.length; i++){
					if(i == 0 || i == 2 || i == 4 || i == 5) sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</p></td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(str1[i] == null ? 0:(Long)str1[i].longValue())+"</p></td>");
				}
			}else {
				for(int i = 0; i < 7; i++){
					if(i == 0 || i == 2 || i == 4 || i == 5) sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</p></td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</p></td>");
				}
			}
			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Currency</td>");
			Double[] str2 = transactionSummaryCurrencyTableValues(userId,fromDate,toDate,prgmId);
			int i1=0;
			if(str2 != null && str2.length > 0){
				for(Double currStr2 : str2){
					if(i1 == 0 || i1 == 4) sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</p></td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(currStr2==null?0.0:(Double)currStr2.doubleValue())+"</p></td>");
					i1++;
				}
			}else {
				for(int i = 0; i < 7; i++){
					if(i1 == 0 || i1 == 4) sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</p></td>");
					else sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</p></td>");
					i1++;
				}
			}
			sb.append("</tr></table>");
			return sb;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}

	private Double[] transactionSummaryCurrencyTableValues(Long userId, String fromDate, String toDate, Long prgmId) {
		Double[] count1 = new Double[7] ;
		try {
			List<Object[]>     count =loyaltyTransactionChildDao.getCurrencyIssuance(userId,fromDate,toDate,prgmId);
			if(count != null && count.size() > 0){
				for(Object[] eachRow:count){
					if(eachRow[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_GIFT)){
						count1[2] = (Double) eachRow[2];
					}else if (eachRow[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)){
						count1[1] = (Double) eachRow[2];	
					}
				}
			}
			Double     countReedm =loyaltyTransactionChildDao.getCurrencyRedeemed(userId,prgmId,fromDate,toDate);
			if(countReedm != 0){
				count1[3] = -countReedm;
			}else if(countReedm == 0){
				count1[3] = 0.0;
			}
			
			Double     countStoreCredit =loyaltyTransactionChildDao.getStoreCredit(userId,prgmId,fromDate,toDate);
			count1[5] = countStoreCredit;
			
			count1[6] = loyaltyTransactionChildDao.getCurrencyLiability(userId,prgmId,fromDate,toDate);
			
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		return count1;
	}

	private Double[] transactionSummaryPointsTableValues(Long userId, String fromDate,String toDate, Long prgmId) {
		try {
			
			Double     count =loyaltyTransactionChildDao.getPointsIssuance(userId,prgmId,fromDate,toDate);
			Double[] count1 = new Double[7] ;
				count1[1] = count;
			count =loyaltyTransactionChildDao.getPointsLiability(userId,prgmId,fromDate,toDate);	
				count1[6] = count;
			Double     countReedm =loyaltyTransactionChildDao.getPointsRedemption(userId,prgmId,fromDate,toDate);
			if(countReedm != 0){
				count1[3] = -countReedm;
			}else if(countReedm == 0){
				count1[3] = 0.0;
			}	
			
			return count1;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		return null;
	}

	private int[] transactionSummaryNumOfTrnTableValues(Long userId, String fromDate,String toDate, Long prgmId) {
		try {
			
			List<Object[]>     count =loyaltyTransactionChildDao.getEnrollmentAndRedemptionCount(userId,fromDate,toDate,prgmId);
			int[] count1 = new int[7] ;
			for(int i=0;i<7;i++){
				count1[i] = new Integer(0);
			}
			int redeemCount = 0;
			if(count != null && count.size() > 0){
				for(Object[] eachRow:count){
					if(eachRow[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
						count1[0] = Integer.parseInt(eachRow[1].toString());
					}else if(eachRow[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
						redeemCount += Integer.parseInt(eachRow[1].toString());
					}else if(eachRow[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)){
						if(eachRow[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)){
							count1[1] = Integer.parseInt(eachRow[1].toString());
						}else if(eachRow[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_GIFT)){
							count1[2] = Integer.parseInt(eachRow[1].toString());
						}

					}
				}
				count1[3] = redeemCount;
			}
			
			
			count = loyaltyTransactionChildDao.getReturnCount(userId,fromDate,toDate,prgmId);
			
			List<Object[]>    countHelper = loyaltyTransactionChildDao.getStoreCreditCount(userId,fromDate,toDate,prgmId);
					
			if(count != null ){
				
				if(countHelper != null)
					count.addAll(loyaltyTransactionChildDao.getStoreCreditCount(userId,fromDate,toDate,prgmId));
				
			}else {
				
				count = loyaltyTransactionChildDao.getStoreCreditCount(userId,fromDate,toDate,prgmId);
			}
			
			if(count != null && count.size() > 0){
				for(Object[] eachRow:count){
					if(eachRow[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL)){
						count1[4] = Integer.parseInt(eachRow[1].toString());
					}else if(eachRow[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)){
						count1[5] = Integer.parseInt(eachRow[1].toString());
					}

				}
			}
			return count1;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		return null;
	}

	private StringBuffer prepareKpiReportHtml(Long userId, String fromDate, String toDate, Long prgId) {
		try{
			logger.info("====D1====");
			StringBuffer sb = new StringBuffer();

			sb.append("<br/><br/> <a style=\"text-decoration:none;color:#000000\" href=\"\" title =\"Visits and Revenues exclude returns\"><b style=\"margin-left:20px;\">KPIs Summary <img src=\"https://www.freeiconspng.com/uploads/info-icon-17.png\" width=\"13\" alt=\"Png Icon Info\" /> </b> </a> <hr style=\"color:#ffffff;\"><br/>");
			
			sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Enrollments</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"
					+ "New Gift-Card Issuances</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits From Loyalty</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue From Loyalty</th></tr><tr>");
			Object[] str = kpiTableValues(userId,fromDate,toDate,prgId);

			Object[] obj = retailProSalesDao.findTotalVisitsAndRevenue(userId, fromDate,toDate);
			double visitByPercnt = 0;
			double revenueByPercent = 0;
			if(str != null && str.length > 0){
				
				if(obj[0]!=null && obj[1]!=null) {
				visitByPercnt = ((((Long)str[2]).doubleValue() - ((Long)obj[0]).doubleValue())/((Long)obj[0]==0 || obj[0] == null?1:(Long)obj[0]))*100;
				revenueByPercent = (((Double)str[3] - (Double)obj[1])/((Double)obj[1]==0 || obj[1]== null ?new Double(1):(Double)obj[1]))*100;
				for(int i = 0; i < str.length; i++){
					if(i==3) {
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)str[i])+"</p>");
					}
					else {
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+str[i]+"</p>");
					}
					if(i==2) sb.append("<p align=\"center\">("+(visitByPercnt < 0 ? roundOffTo2DecPlaces(-visitByPercnt) : roundOffTo2DecPlaces(visitByPercnt))+" % of Total Visits)</p>");
					else if(i==3) sb.append("<p align=\"center\">("+(revenueByPercent < 0 ? roundOffTo2DecPlaces(-revenueByPercent) : roundOffTo2DecPlaces(revenueByPercent))+" % of Total Revenue)</p>");

					sb.append("</td>");

				}
				}
			}else {
				for(int i = 0; i < 4; i++){
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p>");
					if(i==2) sb.append("<p align=\"center\">("+0+" % of Total Visits)</p>");
					else if(i==3) sb.append("<p align=\"center\">("+0+" % of Total Revenue)</p>");
				}
			}
			sb.append("</tr></table>");

			return sb;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}

	@SuppressWarnings("null")
	private Object[] kpiTableValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			Object [] kpiSummaryStr = new Object[4] ;
			
			kpiSummaryStr[0] = loyaltyTransactionChildDao.getKpiSummaryReportEnrollementCount(userId,fromDate,toDate,prgId);
			kpiSummaryStr[1] = loyaltyTransactionChildDao.getKpiSummaryReportGiftCardIssuanceCount(userId,fromDate,toDate,prgId);
			kpiSummaryStr[2] = loyaltyTransactionChildDao.getKpiSummaryReportVisitLoyaltyCount(userId,fromDate,toDate,prgId);
			kpiSummaryStr[3] = loyaltyTransactionChildDao.getKpiSummaryReportRevenueLtyCount(userId,fromDate,toDate,prgId);
			
			
			return kpiSummaryStr;
		} catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		
		return null;
	}
	
	private StringBuffer prepareKPIsDetailedReportHtml(Long userId, String fromDate, String toDate, Long prgId) {
		try{
			logger.info("====D3====");
			StringBuffer sb = new StringBuffer();
			
			List<Object[]> str = kpiDetailedTableValues(userId,fromDate,toDate,prgId);
			List<Object[]> str1 = kpiDetailedRevenueValues(userId,fromDate,toDate,prgId);

			sb.append("<br/><br/> <a style=\"text-decoration:none;color:#000000\" href=\"\" title =\"Visits include return receipts and Revenues are net of reversals\"><b style=\"margin-left:20px;\">Tier KPIs Detailed Report <img src=\"https://www.freeiconspng.com/uploads/info-icon-17.png\" width=\"13\" alt=\"Png Icon Info\" /> </b> </a> <hr style=\"color:#ffffff;\"><br/>");

			if((str == null || str.size() == 0) || (str1 == null || str1.size() == 0)){
				sb.append("<p>Not Available</p>");
				return sb;
			}
			
			/*sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Total Memberships</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrolled Today</th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Upgraded Today</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue</th></tr>");*/
			
			sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Total Memberships</th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue</th></tr>");
			
			Users users = usersDao.findByUserId(userId);
			String country=	users.getCountry();
			
			for (Object[] objects : str) {

				sb.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+objects[0]+"</p>");
				sb.append("<p align=\"center\">("+objects[1]+")</p></td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+objects[2]+"</td>");
				/*sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+objects[3]+"</td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+objects[4]+"</td>");*/

				boolean isFound =  false;
				for (Object[] objects1 : str1) {
					if(objects1[0].equals(objects[3])){
						
						
						if(country.equalsIgnoreCase("India")) {
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+objects1[1]+"</td>");
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><span>&#x20b9;</span> "+roundOffTo2DecPlaces((Double)objects1[2])+"</td></tr>");
						
						}
						else {
						
							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+objects1[1]+"</td>");
							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">$"+roundOffTo2DecPlaces((Double)objects1[2])+"</td></tr>");
						
						}
				
						
				isFound = true;
				break;
					
					
					}
				}
		
				if(!isFound){
					
					if(country.equalsIgnoreCase("India")) {

						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><span>&#x20b9;</span> "+0.00+"</td></tr>");
				
					}
					else {
				
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">$"+0.00+"</td></tr>");
				
					}
				
				}

			}
			sb.append("</table>");
			return sb;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private List<Object[]> getAllTiers(Long prgId,Long userId){
		try{
		List<Object[]> count =loyaltyTransactionChildDao.getAllTiers(prgId,userId);
		return count;
		}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> kpiDetailedTableValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count =loyaltyTransactionChildDao.getTierMemberships(userId,fromDate,toDate,prgId);
			
			return count;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private List<Object[]> kpiDetailedRevenueValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersByProgramId(prgId);
			List<Object[]> count1 =loyaltyTransactionChildDao.getVisitsByTier(userId,fromDate,toDate,prgId, listOfTiers);
			
			return count1;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	
	private StringBuffer prepareStoreKPIsDetailedReportHtml(Long userId, String fromDate, String toDate, Long prgId) {
		try{
			logger.info("====D4====");
		logger.info("----entered into prepareStoreKPIsDetailedReportHtml----");
			
			StringBuffer sb = new StringBuffer();
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Store KPIs Detailed Report</b><hr style=\"color:#ffffff;\"><br/>");
		
			logger.info("----Heading Added---");

			List<Object[]> storeNumbers = getAllStores(userId,fromDate,toDate,prgId);
			logger.info("getAllStores value is"+storeNumbers);
			
			List<Object[]> str = storeKpiDetailedEnrollmentValues(userId,fromDate,toDate,prgId);
			logger.info("storeKpiDetailedEnrollmentValues is"+str);

			List<Object[]> str1 = kpiDetailedIssuanceValues(userId,fromDate,toDate,prgId);
			logger.info("kpiDetailedIssuanceValues is"+str1);

			
			List<Object[]> str2 = kpiDetailedVisitsValues(userId,fromDate,toDate,prgId);
			logger.info("kpiDetailedVisitsValues is"+str2);

			
			
			if(storeNumbers == null || storeNumbers.size() == 0){
				
				logger.info("Store numbere are null");
				sb.append("<p>Not Available</p>");
				return sb;
		
			}

			Object[] obj = retailProSalesDao.findTotalVisitsAndRevenue(userId, fromDate,toDate);
			
			logger.info("findTotalVisitsAndRevenue is"+obj);

			
			double visitByPercnt = 0;
			double revenueByPercent = 0;

			
			Users users = usersDao.findByUserId(userId);
			Long orgid=users.getUserOrganization().getUserOrgId();
			logger.info("organisation id is"+orgid);
	
			 List<OrganizationStores> storeIdList = organizationStoresDao.findByOrganization(orgid);

			 logger.info("Stores list is"+storeIdList);
			 
			for (OrganizationStores org : storeIdList) {
				logger.info("enterd into store map");
				
				storeNameMap.put(org.getHomeStoreId(), org.getStoreName());
			

			}
			
			sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#444444;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th>");
			
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Enrollments</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Gift-Card Issuances</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits From Loyalty</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue From Loyalty</th></tr>");
//			
                    for (Object[] store : storeNumbers) {
				int i = 0;
				logger.info("store name====>"+storeNameMap.get(store[0]));
				
				if(storeNameMap.get(store[0])!=null ) {
				
					logger.info("Entered into Store name mapping if block");
					
					sb.append("<tr><td style=\"border:1px solid #D1D6DA; background:#CA0000; color:#ffffff; padding:5px;font-size:13;text-align:center;\">"+storeNameMap.get(store[0])+"</td>");
				
				}else {
				
					logger.info("Entered into Store name mapping else block");

					sb.append("<tr><td style=\"border:1px solid #D1D6DA; background:#CA0000; color:#ffffff; padding:5px;font-size:13;text-align:center;\">Store ID"+ store[i]+"</td>");
					
	
					}
		
//			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Enrollments</td>");
//
             	boolean isFound = false;
				if(str  != null && str.size() > 0){ //new Enrollments
					for(Object[] object : str){
						
						if(object[0]!=null) {         //added for handle Exception
						if(store[0].toString().equalsIgnoreCase(object[0].toString())){
							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[1]+"</td>");
							isFound = true;
						     break;
						}
					} 
				}
				}
				if(!isFound)sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
			
				
//			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Gift-Card Issuances</td>");
				
				isFound = false;
				if(str1 != null && str1.size() > 0){ //new gift card issurance
					for(Object[] object1 : str1){
						
						if(object1[0]!=null) { //added for handle Exception
						if(store[0].toString().equalsIgnoreCase(object1[0].toString())){
							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object1[1]+"</td>");
							isFound = true;
							break;
						}

						}
					}
				}
				if(!isFound)sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
//		
				//}
				
//			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits From Loyalty</td>");

				isFound = false;
				if(str2 != null && str2.size() > 0){ // visits from loyalty
					for(Object[] object2 : str2){

						if(object2[0]!=null) { //added for handle Exception
						
							if(store[0].toString().equalsIgnoreCase(object2[0].toString())){
							visitByPercnt = ((((Long)object2[1]).doubleValue() - (Long)obj[0])/(obj[0] == null || (Long)obj[0] == 0 ? 1 : (Long)obj[0]))*100;
						
					 		logger.info("----visitByPercnt value is-----"+visitByPercnt);

							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)object2[1]+"</p>");
							sb.append("<p align=\"center\">("+(visitByPercnt < 0 ? roundOffTo2DecPlaces(-visitByPercnt) : roundOffTo2DecPlaces(visitByPercnt))+" % of Total Store Visits)</p></td>");
							isFound = true;
							break;
						}
					}}
				
				}
				if(!isFound){
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p>");
					sb.append("<p align=\"center\">("+0+" % of Total Store Visits)</p></td>");
				}
//			
				
//			sb.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue From Loyalty</td>");
				isFound = false;
				if(str2 != null && str2.size() > 0){ //revenue fom visits
					for(Object[] object3 : str2){
						
						if(object3[0]!=null) { //added for handle Exception
						if(store[0].toString().equalsIgnoreCase(object3[0].toString())){
							revenueByPercent = (((Double)object3[2] - (Double)obj[1])/(obj[1] == null || (Double)obj[1] == 0 ? 1 : (Double)obj[1]))*100;
					 		logger.info("----revenueByPercent value is-----"+revenueByPercent);

					 		sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)object3[2])+"</p>");
							sb.append("<p align=\"center\">("+(revenueByPercent > 0 ? roundOffTo2DecPlaces(revenueByPercent) : roundOffTo2DecPlaces(-revenueByPercent))+" % of Total Store Revenue)</p></td></tr>");
							isFound = true;
							break;
						}
					}
					}	
				}
				if(!isFound){
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.00+"</p>");
					sb.append("<p align=\"center\">("+0+" % of Total Store Visits)</p></td></tr>");
				}
     }
//
			sb.append("</table>");
			logger.info("Final value sb (Store Kpi detailed Report) is"+sb);
	

			return sb;

		}catch (Exception e) {
			logger.error("Exception in Store Kpi detailed Report Method::", e);
		}
		logger.info("---Exit prepareStoreKPIsDetailedReportHtml---- ");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> getAllStores(Long userId, String fromDate,String toDate, Long prgId) {
		try {
		
			logger.info("entred into getAllStores method ");
			List<Object[]> countStore =loyaltyTransactionChildDao.getStoresByType(userId,fromDate,toDate,prgId);
			
			logger.info("countStore value "+countStore);
			
			return countStore;
			}catch (Exception e) {		
			logger.error("Exception in getAllStores Method::",e);
			

			}
		logger.info("exit getAllStores method ");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> storeKpiDetailedEnrollmentValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
		
			logger.info("entred into storeKpiDetailedEnrollmentValues method ");

			List<Object[]> count =loyaltyTransactionChildDao.getEnrollmensByStore(userId,fromDate,toDate,prgId);
			
			logger.info("storeKpiDetailedEnrollmentValues count is"+count);
			return count;
			}catch (Exception e) {		
			logger.error("Exception in storeKpiDetailedEnrollmentValues method ::",e);
		
	
			}
			logger.info("exit storeKpiDetailedEnrollmentValues method ");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> kpiDetailedIssuanceValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			logger.info("entred into kpiDetailedIssuanceValues method ");

			
			List<Object[]> count1 =loyaltyTransactionChildDao.getGiftIssuanceByStore(userId,fromDate,toDate,prgId);
			
			logger.info("kpiDetailedIssuanceValues count is"+count1);
			return count1;
			}catch (Exception e) {		
			logger.error("Exception in kpiDetailedIssuanceValues Method ::",e);
		}
		logger.info("exit kpiDetailedIssuanceValues method ");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> kpiDetailedVisitsValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			logger.info("entred into kpiDetailedVisitsValues method ");

			
			List<Object[]> count2 =loyaltyTransactionChildDao.getVisitsByStore(userId,fromDate,toDate,prgId);
		
			logger.info("kpiDetailedVisitsValues count is"+count2);
		
			return count2;
			}catch (Exception e) {		
			logger.error("Exception in kpiDetailedVisitsValues method ::",e);
		}
		logger.info("exit  kpiDetailedVisitsValues method ");

		return null;
	}
	
	private StringBuffer prepareStoreTransactionDetailedReportHtml(Long userId, String fromDate, String toDate, Long prgId) {
		try{
			
			logger.info("entered into prepareStoreTransactionDetailedReportHtml method");

			
			logger.info("====D5====");
			StringBuffer sb = new StringBuffer();
			
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Store Transactions Detailed Report</b><hr style=\"color:#ffffff;\"><br/>");
			List<Object[]> storeNumbers = getAllStoresForTransaction(userId,fromDate,toDate,prgId);
			logger.info("getAllStoresForTransaction value is"+storeNumbers);

			List<Object[]> str = storeTransactionDetailedValues(userId,fromDate,toDate,prgId);
			logger.info("storeTransactionDetailedValues is"+str);

			
			List<Object[]> str1 = storeTransactionDetailedValues1(userId,fromDate,toDate,prgId);
			logger.info("storeTransactionDetailedValues another method is"+str1);

			
			if(storeNumbers == null || storeNumbers.size() == 0){
				logger.info("Store number are null for prepareStoreTransactionDetailedReportHtmltable" );
				sb.append("<p>Not Available</p>");
				return sb;
			}
			Users users = usersDao.findByUserId(userId);
			Long orgid=users.getUserOrganization().getUserOrgId();
			logger.info("organisation id is"+orgid);
	
			List<OrganizationStores> storeIdList = organizationStoresDao.findByOrganization(orgid);


			for (OrganizationStores org : storeIdList) {
				logger.info("enterd into store map");
				
				storeNameMap.put(org.getHomeStoreId(), org.getStoreName());
			

			}
			
			sb.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#444444; color:#ffffff;\">"
					+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" colspan=\"2\"></th>");
			
			 
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrollment</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Loyalty Issuance</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issuance</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redemption</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Return</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credit</th>");
			sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability</th></tr>");
			
//			
			
			
			for (Object[] store : storeNumbers) {
				int i = 0;
				
				
				logger.info("store name====>"+storeNameMap.get(store[0]));
				if(storeNameMap.get(store[0])!=null ) {
				
					logger.info("Entered into Store name mapping if block");

					sb.append("<tr><th style=\"border:1px solid #D1D6DA; background:#CA0000; padding:5px;font-size:13; color:#ffffff; text-align:center;\" rowspan=\"3\">"+storeNameMap.get(store[0])+"</th>");

				
				
				}else {
				
					logger.info("Entered into Store name mapping else block");

					sb.append("<tr><th style=\"border:1px solid #D1D6DA; background:#CA0000; padding:5px;font-size:13; color:#ffffff; text-align:center;\" rowspan=\"3\">Store ID"+store[i]+"</th>");

					
					
	
					}
				
				
				//	sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+store[i]+"</th>");
				//sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"3\">"+store[i]+"</th>");
//			}
//			sb.append("</tr><tr style=\"background : #444444;color:#ffffff;\">");
//			for (Object[] store : storeNumbers) {
//				sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">No. of Transactions</th>"
//						+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Points</th>"
//						+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Currency</th>");
//			}
//			sb.append("</tr><tr>");

//			sb.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrollment</td>");
//		
//			redeBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redemption</td>");
//			for (Object[] store : storeNumbers) {
				
				
				boolean isEnrollmentDone = false;
				boolean isRedemptionDone = false;
				//points
				StringBuffer pointsRow= new StringBuffer();
				//currency
				StringBuffer currencyRow= new StringBuffer();
				StringBuffer redeBufferNtrans = new StringBuffer();
				StringBuffer redeBufferPoints = new StringBuffer();
				StringBuffer redeBufferCurrency = new StringBuffer();
				
				sb.append("<th style=\"border:1px solid #D1D6DA;padding:5px; background:#444444; font-size:13; color:#ffffff; text-align:center;\">No. of Transactions</th>");
				pointsRow.append("<tr><th style=\"border:1px solid #D1D6DA;  background:#444444; padding:5px; color:#ffffff; font-size:13;text-align:center;\">Points</th>");
				currencyRow.append("<tr><th style=\"border:1px solid #D1D6DA; background:#444444; padding:5px; color:#ffffff; font-size:13;text-align:center;\">Currency</th>");
				
				if(str != null && str.size() > 0){
					for (Object[] object : str) {
						if(store[0].toString().equalsIgnoreCase(object[0].toString())){
							if(object[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
							    sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[2]+"</td>");
								pointsRow.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
								currencyRow.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
								isEnrollmentDone = true;

							}
							else if(object[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
								redeBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[2]+"</td>");
								if((Double)object[3] != 0){
									
//								redeBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size\"<th style=\\\"border:1px solid #D1D6DA;padding\"<th style=\\\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\\\">Currency</th>\":5px;font-size:13;text-align:center;\\\">Currency</th>\":13;text-align:center;\">"+(-((Double)object[3]).longValue())+"</td>");
									
								redeBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(-((Double)object[3]).longValue())+"</td>");
								}else if((Double)object[3] == 0){
									redeBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
								}
								if(((Double)object[4] != 0) && ((Double)object[5] != 0)){
								redeBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(-((Double)object[4]+(Double)object[5]))+"</td>");
								}else if(((Double)object[4] != 0) && ((Double)object[5] == 0)){
									redeBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(-((Double)object[4]+(Double)object[5]))+"</td>");
								}else if(((Double)object[4] == 0) && ((Double)object[5] != 0)){
									redeBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(-((Double)object[4]+(Double)object[5]))+"</td>");
								}else if(((Double)object[4] == 0) && ((Double)object[5] == 0)){
									redeBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
								}
								isRedemptionDone =true;
							}

							if(isEnrollmentDone && isRedemptionDone)break;
						}
					}
				}

				if(!isEnrollmentDone){
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					pointsRow.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					currencyRow.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
				}
				if(!isRedemptionDone){
					redeBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					redeBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					redeBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
				}

			
//			sb.append("</tr>");
//			redeBuffer.append("</tr>"); 

			StringBuffer ltyIssuBufferNtrans = new StringBuffer();
			StringBuffer ltyIssuBufferPoints = new StringBuffer();
			StringBuffer ltyIssuBufferCurrency = new StringBuffer();
//			ltyIssuBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Loyalty Issuance</td>");
			StringBuffer giftIssuBufferNtrans = new StringBuffer();
			StringBuffer giftIssuBufferPoints = new StringBuffer();
			StringBuffer giftIssuBufferCurrency = new StringBuffer();
//			giftIssuBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issuance</td>");
			StringBuffer returnBufferNtrans = new StringBuffer();
			StringBuffer returnBufferPoints = new StringBuffer();
			StringBuffer returnBufferCurrency = new StringBuffer();
//			returnBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Return</td>");
			StringBuffer storeCreditBufferNtrans = new StringBuffer();
			StringBuffer storeCreditBufferPoints = new StringBuffer();
			StringBuffer storeCreditBufferCurrency = new StringBuffer();
//			storeCreditBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credit</td>");
//			for (Object[] store : storeNumbers) {
				boolean isLoyaltyIssuanceDone=false;
				boolean isGiftIssuanceDone=false;
				boolean isReturnDone=false;
				boolean isStoreCreditDone=false;
				if(str1 != null && str1.size() > 0){
					for (Object[] object : str1) {
						if(store[0].toString().equalsIgnoreCase(object[0].toString())){
							if((object[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))){
								if((object[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE))){
									ltyIssuBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[3]+"</td>");
									ltyIssuBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Double)object[4]).longValue()+"</td>");
									ltyIssuBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[5]+"</td>");
									isLoyaltyIssuanceDone = true;
								}else if((object[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT))){
									giftIssuBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[3]+"</td>");
									giftIssuBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
									giftIssuBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[6]+"</td>");
									isGiftIssuanceDone = true;
								}
							}else if((object[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN))){
								if((object[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL))){
									returnBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[3]+"</td>");
									returnBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
									returnBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
									isReturnDone = true;
								}else if((object[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT))){
									storeCreditBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[3]+"</td>");
									storeCreditBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
									storeCreditBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Double)object[5]+(Double)object[6])+"</td>");
									isStoreCreditDone = true;
								}

							}
						}

						if(isLoyaltyIssuanceDone && isGiftIssuanceDone && isReturnDone && isStoreCreditDone)break;
					}
				}

				if(!isLoyaltyIssuanceDone){
					ltyIssuBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					ltyIssuBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					ltyIssuBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
				}

				if(!isGiftIssuanceDone){
					giftIssuBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					giftIssuBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+"--"+"</td>");
					giftIssuBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
				}

				if(!isReturnDone){
					returnBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					returnBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					returnBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
				}

				if(!isStoreCreditDone){
					storeCreditBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					storeCreditBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					storeCreditBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
				}

			

//			ltyIssuBuffer.append("</tr>");
//			giftIssuBuffer.append("</tr>");
//			returnBuffer.append("</tr>");
//			storeCreditBuffer.append("</tr>");
				sb.append(ltyIssuBufferNtrans);
				sb.append(giftIssuBufferNtrans);
				sb.append(redeBufferNtrans);
				sb.append(returnBufferNtrans);
				sb.append(storeCreditBufferNtrans);

				pointsRow.append(ltyIssuBufferPoints);
				pointsRow.append(giftIssuBufferPoints);
				pointsRow.append(redeBufferPoints);
				pointsRow.append(returnBufferPoints);
				pointsRow.append(storeCreditBufferPoints);
				
				currencyRow.append(ltyIssuBufferCurrency);
				currencyRow.append(giftIssuBufferCurrency);
				currencyRow.append(redeBufferCurrency);
				currencyRow.append(returnBufferCurrency);
				currencyRow.append(storeCreditBufferCurrency);

			StringBuffer liabilityBufferNtrans = new StringBuffer();
			StringBuffer liabilityBufferPoints = new StringBuffer();
			StringBuffer liabilityBufferCurrency = new StringBuffer();
			
//			liabilityBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability</td>");

			str1 = storeLiabilityValues(userId,fromDate,toDate,prgId);
		
			logger.info("storeLiabilityValues is "+str1);

			boolean isFound = false;
//			for (Object[] store : storeNumbers) {
				isFound = false;
				if(str != null && str1.size() > 0){
					for (Object[] object : str1) {

						if(store[0].toString().equalsIgnoreCase(object[0].toString())){
							liabilityBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
							liabilityBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Long)object[1])+"</td></tr>");
							liabilityBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+object[2]+"</td></tr>");
							isFound = true;
							break;
						}
					}
				}
				if(!isFound){
					liabilityBufferNtrans.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
					liabilityBufferPoints.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td></tr>");
					liabilityBufferCurrency.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td></tr>");
				}
				sb.append(liabilityBufferNtrans);
				pointsRow.append(liabilityBufferPoints);
				currencyRow.append(liabilityBufferCurrency);
				sb.append(pointsRow);
				sb.append(currencyRow);
				
			}

//			liabilityBuffer.append("</tr>");

//			sb.append(ltyIssuBuffer);
//			sb.append(giftIssuBuffer);
//			sb.append(redeBuffer);
//			sb.append(returnBuffer);
//			sb.append(storeCreditBuffer);
//			sb.append(liabilityBuffer);

			sb.append("</table>");
			return sb;
		}catch (Exception e) {
			logger.error("Exception in prepareStoreTransactionDetailedReportHtml method  ::",e);
		}
		
		logger.info("entered into prepareStoreTransactionDetailedReportHtml method");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> getAllStoresForTransaction(Long userId, String fromDate,String toDate, Long prgId) {
		try {
		logger.info("entered getAllStoresForTransaction method");
			List<Object[]> countStore =loyaltyTransactionChildDao.getStoreByTransaction(userId,fromDate,toDate,prgId);
			
			logger.info(" countStore for getAllStoresForTransaction method is "+countStore);
	
			return countStore;
			}catch (Exception e) {		
			logger.error("Exception in getAllStoresForTransaction::",e);
		}
		logger.info("exit getAllStoresForTransaction method");

		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> storeTransactionDetailedValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			logger.info("entered storeTransactionDetailedValues method");

			
			List<Object[]> count1 =loyaltyTransactionChildDao.getEnrollmentsByStore(userId,fromDate,toDate,prgId);
			logger.info(" count storeTransactionDetailedValues is"+count1);
			return count1;
			}catch (Exception e) {		
			logger.error("Exception in storeTransactionDetailedValues::",e);
		}
		logger.info("exit storeTransactionDetailedValues method");

		return null;
	}
	
	
	private List<Object[]> storeLiabilityValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count1 =loyaltyTransactionChildDao.getLiabilityByStore(userId,fromDate,toDate,prgId);
			logger.info("count for storeLiabilityValues is"+count1);
			
			return count1;
			}catch (Exception e) {		
			logger.error("Exception in storeLiabilityValues ::",e);
		}
		return null;
	}
	
	
	@SuppressWarnings("null")
	private List<Object[]> storeTransactionDetailedValues1(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			logger.info("entered storeTransactionDetailedValues1 another method");

			
			List<Object[]> count1 =loyaltyTransactionChildDao.getIssuanceAndStoreCreditByStore(userId,fromDate,toDate,prgId);
			
			logger.info("count for storeTransactionDetailedValues1"+count1);
			count1.addAll(loyaltyTransactionChildDao.getReturnByStore(userId,fromDate,toDate,prgId));
			
			return count1;
			}catch (Exception e) {		
			logger.error("Exception in storeTransactionDetailedValues1 method::",e);
		}
		logger.info("exit storeTransactionDetailedValues1 another method");

		return null;
	}
	
	private StringBuffer weeklyKpiSummaryHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
			logger.info("====W1====");
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("<br/><br/><b style=\"margin-left:20px;\">KPIs Summary</b><hr style=\"color:#ffffff;\"><br/>");

			strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Enrollments</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Gift-Card Issuances</th>"
					+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits From Loyalty</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue From Loyalty</th></tr>");
			strBuff.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This Week</td>");

			String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);


			Object[] str = kpiTableValues(userId,fromDate,toDate,prgId);
			Object[] obj = retailProSalesDao.findTotalVisitsAndRevenue(userId, fromDate,toDate);
			double visitByPercnt = 0;
			double revenueByPercent = 0;
			if(str != null && str.length > 0){
				
				if(obj[0]!=null && obj[1]!=null) {
				visitByPercnt = ((((Long)str[2]).doubleValue() - (obj[0]==null || (Long)obj[0] == 0 ? 0:(Long)obj[0]))/((Long)obj[0]==0?1:(Long)obj[0]))*100;
				revenueByPercent = (((Double)str[3] - (obj[1] == null || (Double)obj[1] == 0 ? 0 :(Double)obj[1]))/((Double)obj[1]==0?1:(Double)obj[1]))*100;
				for(int i = 0; i < str.length; i++){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+str[i]+"</p>");
					if(i==2 && visitByPercnt != 0){ 
						strBuff.append("<p align=\"center\">("+(visitByPercnt >= 0 ? roundOffTo2DecPlaces(visitByPercnt) : (roundOffTo2DecPlaces(-visitByPercnt)))+" % of Total Visits)</p>");
					}else if(i==2 && visitByPercnt == 0){
						strBuff.append("<p align=\"center\">("+0+" % of Total Visits)</p>");
					}
					if(i==3 && revenueByPercent != 0){ 
						strBuff.append("<p align=\"center\">("+(revenueByPercent >= 0 ? roundOffTo2DecPlaces(revenueByPercent) : (roundOffTo2DecPlaces(-revenueByPercent)))+" % of Total Revenue)</p>");
					}else if(i==3 && revenueByPercent == 0){
						strBuff.append("<p align=\"center\">("+0+" % of Total Revenue)</p>");
					}
					strBuff.append("</td>");
				}
				}
			
			}else {
				for(int i = 0; i < 4; i++){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p>");
					if(i==2) strBuff.append("<p align=\"center\">("+0+" % of Total Visits)</p>");
					else if(i==3) strBuff.append("<p align=\"center\">("+0+" % of Total Revenue)</p>");
					strBuff.append("</td>");
				}
			}
			strBuff.append("</tr><tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</td>");

			from.add(Calendar.DAY_OF_MONTH, -7);
			till.add(Calendar.DAY_OF_MONTH, -7);

			fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			from.add(Calendar.DAY_OF_MONTH, 7);
			till.add(Calendar.DAY_OF_MONTH, 7);


			Object[] str1 = kpiTableValues(userId,fromDate,toDate,prgId);
			if(str1 != null && str1.length > 0){
				for(int i = 0; i < str1.length; i++){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+str1[i]+"</p>");
					strBuff.append("</td>");
				}
			}else {
				for(int i = 0; i < 4; i++){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p>");
					strBuff.append("</td>");
				}
			}
			strBuff.append("</tr></table>");

			return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private StringBuffer weeklyTransactionSummaryReportHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
		logger.info("====W2====");
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<br/><br/><b style=\"margin-left:20px;\">Transactions Summary</b><hr style=\"color:#ffffff;\"><br/>");
		
		strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>"
				+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">No. of Transactions</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Points</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Currency</th></tr>");
		strBuff.append("<tr><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This week</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>"
				+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This week</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>"
				+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This week</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th></tr>");
		strBuff.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrollment</td>");
		
		String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
		String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		List<Object[]> str = weeklyTransactionSummaryValues(userId,fromDate,toDate,prgId);
		List<Object[]> str1 = weeklyTransactionSummaryValues1(userId,fromDate,toDate,prgId);
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
			for (Object[] object : str) {
				if(object[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))
					map.put(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT, object);
				else if (object[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION))
					map.put(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION, object);
			}
		
			for(Object[] object1 : str1){
				if(object1[0] == null)continue;
				if((object1[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))){
					if((object1[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE))){
						map.put("Loyalty Issuance", object1);
					}else if(object1[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){
						map.put("Gift Issuance", object1);
					}
				}
				else if((object1[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN))){
					if((object1[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL))){
						map.put(OCConstants.LOYALTY_TRANSACTION_RETURN, object1);
					}
					else if((object1[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)))
						map.put("Store Credits", object1);
				}
				else{
					map.put("Liability", object1);
				}
			}
		from.add(Calendar.DAY_OF_MONTH, -7);
		till.add(Calendar.DAY_OF_MONTH, -7);
		
		fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
		toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		from.add(Calendar.DAY_OF_MONTH, 7);
		till.add(Calendar.DAY_OF_MONTH, 7);
		
		List<Object[]> str2 = weeklyTransactionSummaryValues(userId,fromDate,toDate,prgId);
		List<Object[]> str3 = weeklyTransactionSummaryValues1(userId,fromDate,toDate,prgId);
		
		HashMap<String, Object[]> map1 = new HashMap<String, Object[]>();
			for (Object[] object2 : str2) {
				if(object2[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))
					map1.put(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT, object2);
				else if (object2[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION))
					map1.put(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION, object2);
			}
		
			for(Object[] object3 : str3){
				if(object3[0]==null)continue;
				if((object3[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))){
					if((object3[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE))){
						map1.put("Loyalty Issuance", object3);
					}else if(object3[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){
						map1.put("Gift Issuance", object3);
					}
				}
				else if((object3[0].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN))){
					if((object3[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL))){
						map1.put(OCConstants.LOYALTY_TRANSACTION_RETURN, object3);
					}
					else if((object3[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)))
						map1.put("Store Credits", object3);
				}
				else {
					map1.put("Liability", object3);
				}
			}
		Object enrObjThisWeek[] = map.get(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
		Object enrObjLastWeek[] = map1.get(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
		double perEnrollTrans = 0.0;
		perEnrollTrans = (((enrObjThisWeek==null || enrObjThisWeek[1] == null || (Long)enrObjThisWeek[1] == 0 ? 0 : ((Long)enrObjThisWeek[1]).doubleValue()) 
				- (enrObjLastWeek == null || enrObjLastWeek[1] == null || (Long)enrObjLastWeek[1] == 0 ? 0 : ((Long)enrObjLastWeek[1])))
				/(enrObjLastWeek == null || enrObjLastWeek[1] == null || (Long)enrObjLastWeek[1] == 0 ? 1 : ((Long)enrObjLastWeek[1]).doubleValue()))*100;
		if(perEnrollTrans != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(enrObjThisWeek==null || enrObjThisWeek[1]==null?0:((Long)enrObjThisWeek[1]))+"</p><p align=\"center\">("+(perEnrollTrans < 0 ? roundOffTo2DecPlaces(-perEnrollTrans)+"&darr;" : (roundOffTo2DecPlaces(perEnrollTrans)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(enrObjLastWeek==null || enrObjLastWeek[1] == null? 0: ((Long)enrObjLastWeek[1]))+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}else if(perEnrollTrans == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(enrObjThisWeek==null || enrObjThisWeek[1]==null?0:((Long)enrObjThisWeek[1]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(enrObjLastWeek==null || enrObjLastWeek[1] == null? 0: ((Long)enrObjLastWeek[1]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
			}
		strBuff.append("</tr><tr>");
		Object ltyIssuThisWeek[] = map.get("Loyalty Issuance");
		Object ltyIssuLastWeek[] = map1.get("Loyalty Issuance");
		double transLI = 0;
		double pointsLI = 0;
		double currLI = 0;
		transLI = (((ltyIssuThisWeek == null || ltyIssuThisWeek[2] == null || (Long)ltyIssuThisWeek[2] == 0 ? 0 : ((Long)ltyIssuThisWeek[2]).doubleValue()) 
				- (ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null || (Long)ltyIssuLastWeek[2] == 0 ? 0 : ((Long)ltyIssuLastWeek[2])))
				/(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null || (Long)ltyIssuLastWeek[2] == 0 ? 1 : ((Long)ltyIssuLastWeek[2]).doubleValue()))*100;
		
		pointsLI = (((ltyIssuThisWeek == null || ltyIssuThisWeek[3] == null || (Long)ltyIssuThisWeek[3] == 0 ? 0 : ((Long)ltyIssuThisWeek[3]).doubleValue()) 
				- (ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null || (Long)ltyIssuLastWeek[3] == 0 ? 0 : ((Long)ltyIssuLastWeek[3])))
				/(ltyIssuLastWeek == null || ltyIssuLastWeek[3]==null || (Long)ltyIssuLastWeek[3]== 0 ? 1 : ((Long)ltyIssuLastWeek[3])))*100;
		
		currLI = (((ltyIssuThisWeek == null || ltyIssuThisWeek[4] == null || (Double)ltyIssuThisWeek[4] == 0 ? 0 : (((Double)ltyIssuThisWeek[4]))) 
				- (ltyIssuLastWeek == null || ltyIssuLastWeek[4]==null || (Double)ltyIssuLastWeek[4] == 0 ? 0 : ((Double)ltyIssuLastWeek[4])))
				/(ltyIssuLastWeek == null || ltyIssuLastWeek[4]==null || (Double)ltyIssuLastWeek[4]== 0 ? 1 : ((Double)ltyIssuLastWeek[4])))*100;
		
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Loyalty Issuance</td>");
		if(transLI != 0 && pointsLI != 0 && currLI != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+(transLI < 0 ? roundOffTo2DecPlaces(-transLI)+"&darr;" : (roundOffTo2DecPlaces(transLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+(pointsLI < 0 ? roundOffTo2DecPlaces(-pointsLI)+"&darr;" : (roundOffTo2DecPlaces(pointsLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+(currLI < 0 ? roundOffTo2DecPlaces(-currLI)+"&darr;" : (roundOffTo2DecPlaces(currLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI == 0 && pointsLI != 0 && currLI != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+(pointsLI < 0 ? roundOffTo2DecPlaces(-pointsLI)+"&darr;" : (roundOffTo2DecPlaces(pointsLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+(currLI < 0 ? roundOffTo2DecPlaces(-currLI)+"&darr;" : (roundOffTo2DecPlaces(currLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI != 0 && pointsLI == 0 && currLI != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+(transLI < 0 ? roundOffTo2DecPlaces(-transLI)+"&darr;" : (roundOffTo2DecPlaces(transLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+(currLI < 0 ? roundOffTo2DecPlaces(-currLI)+"&darr;" : (roundOffTo2DecPlaces(currLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI != 0 && pointsLI != 0 && currLI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+(transLI < 0 ? roundOffTo2DecPlaces(-transLI)+"&darr;" : (roundOffTo2DecPlaces(transLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+(pointsLI < 0 ? roundOffTo2DecPlaces(-pointsLI)+"&darr;" : (roundOffTo2DecPlaces(pointsLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI == 0 && pointsLI == 0 && currLI != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+(currLI < 0 ? roundOffTo2DecPlaces(-currLI)+"&darr;" : (roundOffTo2DecPlaces(currLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI != 0 && pointsLI == 0 && currLI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+(transLI < 0 ? roundOffTo2DecPlaces(-transLI)+"&darr;" : (roundOffTo2DecPlaces(transLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI == 0 && pointsLI != 0 && currLI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+(pointsLI < 0 ? roundOffTo2DecPlaces(-pointsLI)+"&darr;" : (roundOffTo2DecPlaces(pointsLI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}else if(transLI == 0 && pointsLI == 0 && currLI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[2]==null?0:(Long)ltyIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[2] == null ? 0:(Long)ltyIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[3]==null?0:(Long)ltyIssuThisWeek[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[3] == null ? 0:((Long)ltyIssuLastWeek[3]))+"</td>"
							+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(ltyIssuThisWeek == null || ltyIssuThisWeek[4]==null?0:((Double)ltyIssuThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(ltyIssuLastWeek == null || ltyIssuLastWeek[4] == null ? 0:((Double)ltyIssuLastWeek[4]))+"</td>");
		}
		strBuff.append("</tr><tr>");
		Object giftIssuThisWeek[] = map.get("Gift Issuance");
		Object giftIssuLastWeek[] = map1.get("Gift Issuance");
		double transGI = 0;
		double currGI = 0;
		transGI = (((giftIssuThisWeek == null || giftIssuThisWeek[2]==null || (Long)giftIssuThisWeek[2]== 0 ? 0 : ((Long)giftIssuThisWeek[2]).doubleValue()) 
				- (giftIssuLastWeek == null || giftIssuLastWeek[2]==null || (Long)giftIssuLastWeek[2]== 0 ? 0 : ((Long)giftIssuLastWeek[2])))
				/(giftIssuLastWeek == null || giftIssuLastWeek[2]==null || (Long)giftIssuLastWeek[2]== 0 ? 1 : ((Long)giftIssuLastWeek[2]).doubleValue()))*100;
		
		currGI = (((giftIssuThisWeek == null || giftIssuThisWeek[4] == null || (Double)giftIssuThisWeek[4] == 0 ? 0 : (((Double)giftIssuThisWeek[4]).doubleValue())) 
				- (giftIssuLastWeek == null || giftIssuLastWeek[4] == null || (Double)giftIssuLastWeek[4] == 0? 0 : ((Double)giftIssuLastWeek[4])))
				/(giftIssuLastWeek == null || giftIssuLastWeek[4]==null || (Double)giftIssuLastWeek[4] == 0 ? 1 : ((Double)giftIssuLastWeek[4])))*100;
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issuance</td>");
		if(transGI != 0 && currGI != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || giftIssuThisWeek[2] == null ? 0:(Long)giftIssuThisWeek[2])+"</p><p align=\"center\">("+(transGI < 0 ? roundOffTo2DecPlaces(-transGI)+"&darr;" : (roundOffTo2DecPlaces(transGI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Long)giftIssuLastWeek[2] == null ? 0:(Long)giftIssuLastWeek[2])+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || (Double)giftIssuThisWeek[4] == null ?0:(Double)giftIssuThisWeek[4])+"</p><p align=\"center\">("+(currGI < 0 ? roundOffTo2DecPlaces(-currGI)+"&darr;" : (roundOffTo2DecPlaces(currGI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Double)giftIssuLastWeek[4] == null ? 0:(Double)giftIssuLastWeek[4])+"</td>");
		}else if(transGI == 0 && currGI != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || giftIssuThisWeek[2] == null ? 0:(Long)giftIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Long)giftIssuLastWeek[2] == null ? 0:(Long)giftIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || (Double)giftIssuThisWeek[4] == null ?0:(Double)giftIssuThisWeek[4])+"</p><p align=\"center\">("+(currGI < 0 ? roundOffTo2DecPlaces(-currGI)+"&darr;" : (roundOffTo2DecPlaces(currGI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Double)giftIssuLastWeek[4] == null ? 0:(Double)giftIssuLastWeek[4])+"</td>");
		}else if(transGI != 0 && currGI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || giftIssuThisWeek[2] == null ? 0:(Long)giftIssuThisWeek[2])+"</p><p align=\"center\">("+(transGI < 0 ? roundOffTo2DecPlaces(-transGI)+"&darr;" : (roundOffTo2DecPlaces(transGI)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Long)giftIssuLastWeek[2] == null ? 0:(Long)giftIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || (Double)giftIssuThisWeek[4] == null ?0:(Double)giftIssuThisWeek[4])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Double)giftIssuLastWeek[4] == null ? 0:(Double)giftIssuLastWeek[4])+"</td>");
		}else if(transGI == 0 && currGI == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || giftIssuThisWeek[2] == null ? 0:(Long)giftIssuThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Long)giftIssuLastWeek[2] == null ? 0:(Long)giftIssuLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(giftIssuThisWeek == null || (Double)giftIssuThisWeek[4] == null ?0:(Double)giftIssuThisWeek[4])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(giftIssuLastWeek == null || (Double)giftIssuLastWeek[4] == null ? 0:(Double)giftIssuLastWeek[4])+"</td>");
		}
		strBuff.append("</tr><tr>");
		Object redemptionThisWeek[] = map.get(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
		Object redemptionLastWeek[] = map1.get(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
		double transcRedem = 0;
		double pointsRedem = 0;
		double currRedem = 0.0;
		transcRedem = (((redemptionThisWeek == null || redemptionThisWeek[1] == null || (Long)redemptionThisWeek[1]== 0 ? 0:((Long)redemptionThisWeek[1]).doubleValue()) 
				- (redemptionLastWeek == null || redemptionLastWeek[1] == null || (Long)redemptionLastWeek[1] == 0 ? 0 : ((Long)redemptionLastWeek[1])))
				/(redemptionLastWeek == null || redemptionLastWeek[1] == null || (Long)redemptionLastWeek[1] == 0 ? 1 : ((Long)redemptionLastWeek[1])))*100;
		
		pointsRedem = (((redemptionThisWeek == null || redemptionThisWeek[2] == null || (Long)redemptionThisWeek[2] == 0 ? 0 : ((Long)redemptionThisWeek[2]).doubleValue()) 
				- (redemptionLastWeek == null || redemptionLastWeek[2] == null || (Long)redemptionLastWeek[2] == 0 ? 0 : ((Long)redemptionLastWeek[2])))
				/(redemptionLastWeek == null || redemptionLastWeek[2] == null || (Long)redemptionLastWeek[2] == 0 ? 1 : ((Long)redemptionLastWeek[2])))*100;
		
		double divider = ((redemptionLastWeek == null || redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ? 0 : ((Double)redemptionLastWeek[3])) + (redemptionLastWeek == null || redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ? 0 : ((Double)redemptionLastWeek[4])));
		currRedem = ((((redemptionThisWeek == null || redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ? 0 : ((Double)redemptionThisWeek[3])) + (redemptionThisWeek == null || redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ? 0 : ((Double)redemptionThisWeek[4]))) - (divider == 0 ? 0 : divider)) / (divider==0?1:divider))*100;
		
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redemptions</td>");
		if(transcRedem != 0 && pointsRedem != 0 && currRedem != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+(transcRedem < 0 ? roundOffTo2DecPlaces(-transcRedem)+"&darr;" : (roundOffTo2DecPlaces(transcRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+(pointsRedem < 0 ? roundOffTo2DecPlaces(-pointsRedem)+"&darr;" : (roundOffTo2DecPlaces(pointsRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+(currRedem < 0 ? roundOffTo2DecPlaces(-currRedem)+"&darr;" : (roundOffTo2DecPlaces(currRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem == 0 && pointsRedem != 0 && currRedem != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+(pointsRedem < 0 ? roundOffTo2DecPlaces(-pointsRedem)+"&darr;" : (roundOffTo2DecPlaces(pointsRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+(currRedem < 0 ? roundOffTo2DecPlaces(-currRedem)+"&darr;" : (roundOffTo2DecPlaces(currRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem != 0 && pointsRedem == 0 && currRedem != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+(transcRedem < 0 ? roundOffTo2DecPlaces(-transcRedem)+"&darr;" : (roundOffTo2DecPlaces(transcRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+(currRedem < 0 ? roundOffTo2DecPlaces(-currRedem)+"&darr;" : (roundOffTo2DecPlaces(currRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem != 0 && pointsRedem != 0 && currRedem == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+(transcRedem < 0 ? roundOffTo2DecPlaces(-transcRedem)+"&darr;" : (roundOffTo2DecPlaces(transcRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+(pointsRedem < 0 ? roundOffTo2DecPlaces(-pointsRedem)+"&darr;" : (roundOffTo2DecPlaces(pointsRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem == 0 && pointsRedem == 0 && currRedem != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+(currRedem < 0 ? roundOffTo2DecPlaces(-currRedem)+"&darr;" : (roundOffTo2DecPlaces(currRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem != 0 && pointsRedem == 0 && currRedem == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+(transcRedem < 0 ? roundOffTo2DecPlaces(-transcRedem)+"&darr;" : (roundOffTo2DecPlaces(transcRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem == 0 && pointsRedem != 0 && currRedem == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+(pointsRedem < 0 ? roundOffTo2DecPlaces(-pointsRedem)+"&darr;" : (roundOffTo2DecPlaces(pointsRedem)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}else if(transcRedem == 0 && pointsRedem == 0 && currRedem == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[1] == null ?0:redemptionThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[1] ==null  ?0:(Long)redemptionLastWeek[1])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(redemptionThisWeek == null || redemptionThisWeek[2] == null ?0:(-(Long)redemptionThisWeek[2]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(redemptionLastWeek == null || redemptionLastWeek[2] ==null  ?0:(-(Long)redemptionLastWeek[2]))+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((redemptionThisWeek == null || (Double)redemptionThisWeek[3] == null || (Double)redemptionThisWeek[3] == 0 ?0.0:(-(Double)redemptionThisWeek[3]))+(redemptionThisWeek == null || (Double)redemptionThisWeek[4] == null || (Double)redemptionThisWeek[4] == 0 ?0.0:(-(Double)redemptionThisWeek[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((redemptionLastWeek == null || (Double)redemptionLastWeek[3] == null || (Double)redemptionLastWeek[3] == 0 ?0.0:(-(Double)redemptionLastWeek[3]))+(redemptionLastWeek == null || (Double)redemptionLastWeek[4] == null || (Double)redemptionLastWeek[4] == 0 ?0.0:(-(Double)redemptionLastWeek[4]))+"</td>")));
		}
		strBuff.append("</tr><tr>");
		Object returnThisWeek[] = map.get(OCConstants.LOYALTY_TRANSACTION_RETURN);
		Object returnLastWeek[] = map1.get(OCConstants.LOYALTY_TRANSACTION_RETURN);
		double transcReturn = 0;
		/*int pointsReturn = 0;
		Double currReturn = 0.0;*/
		transcReturn = (((returnThisWeek == null || returnThisWeek[2] == null || (Long)returnThisWeek[2] == 0 ? 0 : ((Long)returnThisWeek[2]).doubleValue()) 
				- (returnLastWeek == null || returnLastWeek[2] == null || (Long)returnLastWeek[2] == 0 ? 0 : ((Long)returnLastWeek[2])))
				/((returnLastWeek == null || returnLastWeek[2] == null || (Long)returnLastWeek[2] == 0 ? 1 : ((Long)returnLastWeek[2]).doubleValue())))*100;
		
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Returns</td>");
		if(transcReturn != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(returnThisWeek == null || returnThisWeek[2] == null ?0:returnThisWeek[2])+"</p><p align=\"center\">("+(transcReturn < 0 ? roundOffTo2DecPlaces(-transcReturn)+"&darr;" : (roundOffTo2DecPlaces(transcReturn)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(returnLastWeek == null || returnLastWeek[2] == null?0:(Long)returnLastWeek[2])+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}else if(transcReturn == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(returnThisWeek == null || returnThisWeek[2] == null ?0:returnThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(returnLastWeek == null || returnLastWeek[2] == null?0:(Long)returnLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
		}
		strBuff.append("</tr><tr>");
		Object storeCreditThisWeek[] = map.get("Store Credits");
		Object storeCreditLastWeek[] = map1.get("Store Credits");
		double transcStrCrdt = 0;
		double currStrCrdt = 0.0;
		transcStrCrdt = (((storeCreditThisWeek == null || storeCreditThisWeek[2] == null || (Long)storeCreditThisWeek[2] == 0 ? 0 : (Long)storeCreditThisWeek[2]) 
				- (storeCreditLastWeek == null || storeCreditLastWeek[2] == null || (Long)storeCreditLastWeek[2] == 0 ? 0 : ((Long)storeCreditLastWeek[2]).doubleValue()))
				/((storeCreditLastWeek == null || storeCreditLastWeek[2] == null || (Long)storeCreditLastWeek[2]== 0 ? 1 : ((Long)storeCreditLastWeek[2]).doubleValue())))*100;
		
		/*double divider1 = ((storeCreditLastWeek == null || storeCreditLastWeek[4]==null?0:((Double)storeCreditLastWeek[4]))+(storeCreditLastWeek == null || storeCreditLastWeek[5]==null?0:((Double)storeCreditLastWeek[5])));
		currStrCrdt = (((storeCreditThisWeek == null || storeCreditThisWeek[4]==null?0:((Double)storeCreditThisWeek[4]))+(storeCreditThisWeek == null || storeCreditThisWeek[5]==null?0:((Double)storeCreditThisWeek[5])))/(divider1==0?1:divider1))*100;*/
		currStrCrdt = ((storeCreditThisWeek == null || storeCreditThisWeek[6] == null || (Double)storeCreditThisWeek[6] == 0 ? 0 : ((Double)storeCreditThisWeek[6]) 
				- (storeCreditLastWeek == null || storeCreditLastWeek[6] == null || (Double)storeCreditLastWeek[6] == 0 ? 0 : ((Double)storeCreditLastWeek[6])))
				/(storeCreditLastWeek == null || storeCreditLastWeek[6]==null || (Double)storeCreditLastWeek[6]== 0 ? 1 : ((Double)storeCreditLastWeek[6])))*100;
		
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credits</td>");
		if(transcStrCrdt != 0 && currStrCrdt != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(storeCreditThisWeek == null || storeCreditThisWeek[2] == null ?0:(Long)storeCreditThisWeek[2])+"</p><p align=\"center\">("+(transcStrCrdt < 0 ? roundOffTo2DecPlaces(-transcStrCrdt)+"&darr;" : (roundOffTo2DecPlaces(transcStrCrdt)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(storeCreditLastWeek == null || storeCreditLastWeek[2] == null ?0:(Long)storeCreditLastWeek[2])+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((storeCreditThisWeek == null || storeCreditThisWeek[6] == null ?0.0:((Double)storeCreditThisWeek[6]))+"</p><p align=\"center\">("+(currStrCrdt < 0 ? roundOffTo2DecPlaces(-currStrCrdt)+"&darr;" : (roundOffTo2DecPlaces(currStrCrdt)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((storeCreditLastWeek == null || (Double)storeCreditLastWeek[6] == null ?0.0:((Double)storeCreditLastWeek[6]))+"</td>")));
		}else if(transcStrCrdt == 0 && currStrCrdt != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(storeCreditThisWeek == null || storeCreditThisWeek[2] == null ?0:(Long)storeCreditThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(storeCreditLastWeek == null || storeCreditLastWeek[2] == null ?0:(Long)storeCreditLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((storeCreditThisWeek == null || storeCreditThisWeek[6] == null ?0.0:((Double)storeCreditThisWeek[6]))+"</p><p align=\"center\">("+(currStrCrdt < 0 ? roundOffTo2DecPlaces(-currStrCrdt)+"&darr;" : (roundOffTo2DecPlaces(currStrCrdt)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((storeCreditLastWeek == null || (Double)storeCreditLastWeek[6] == null ?0.0:((Double)storeCreditLastWeek[6]))+"</td>")));
		}else if(transcStrCrdt != 0 && currStrCrdt == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(storeCreditThisWeek == null || storeCreditThisWeek[2] == null ?0:(Long)storeCreditThisWeek[2])+"</p><p align=\"center\">("+(transcStrCrdt < 0 ? roundOffTo2DecPlaces(-transcStrCrdt)+"&darr;" : (roundOffTo2DecPlaces(transcStrCrdt)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(storeCreditLastWeek == null || storeCreditLastWeek[2] == null ?0:(Long)storeCreditLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((storeCreditThisWeek == null || storeCreditThisWeek[6] == null ?0.0:((Double)storeCreditThisWeek[6]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((storeCreditLastWeek == null || (Double)storeCreditLastWeek[6] == null ?0.0:((Double)storeCreditLastWeek[6]))+"</td>")));
		}else if(transcStrCrdt == 0 && currStrCrdt == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(storeCreditThisWeek == null || storeCreditThisWeek[2] == null ?0:(Long)storeCreditThisWeek[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(storeCreditLastWeek == null || storeCreditLastWeek[2] == null ?0:(Long)storeCreditLastWeek[2])+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((storeCreditThisWeek == null || storeCreditThisWeek[6] == null ?0.0:((Double)storeCreditThisWeek[6]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((storeCreditLastWeek == null || (Double)storeCreditLastWeek[6] == null ?0.0:((Double)storeCreditLastWeek[6]))+"</td>")));
		}
		strBuff.append("</tr><tr>");
		Object liabilityThisWeek[] = map.get("Liability");
		Object liabilityLastWeek[] = map1.get("Liability");
		double pointsLblty = 0;
		double currLblty = 0.0;
		pointsLblty = (((liabilityThisWeek == null || liabilityThisWeek[0] == null || (Double)liabilityThisWeek[0] == 0 ? 0 : ((Double)liabilityThisWeek[0])) 
				- (liabilityLastWeek == null || liabilityLastWeek[0] == null || (Double)liabilityLastWeek[0] == 0 ? 0 : ((Double)liabilityLastWeek[0])))
				/(liabilityLastWeek == null || liabilityLastWeek[0] == null || (Double)liabilityLastWeek[0] == 0 ? 1 : ((Double)liabilityLastWeek[0])))*100;
		
		currLblty = (((liabilityThisWeek == null || liabilityThisWeek[1] == null || (Double)liabilityThisWeek[1] == 0 ? 0 : ((Double)liabilityThisWeek[1])) 
				- (liabilityLastWeek == null || liabilityLastWeek[1] == null || (Double)liabilityLastWeek[1] == 0 ? 0 : ((Double)liabilityLastWeek[1])))
				/(liabilityLastWeek == null || liabilityLastWeek[1] == null || (Double)liabilityLastWeek[1] == 0 ? 1 : ((Double)liabilityLastWeek[1])))*100;
		
		strBuff.append("<td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability</td>");
		if(pointsLblty != 0 && currLblty != 0){
		strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[0] == null ?0:((Double)liabilityThisWeek[0]).longValue())+"</p><p align=\"center\">("+(pointsLblty < 0 ? roundOffTo2DecPlaces(-pointsLblty)+"&darr;" : (roundOffTo2DecPlaces(pointsLblty)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[0] == null ?0:((Double)liabilityLastWeek[0]).longValue())+"</td>"
				+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[1] == null ?0:(Double)liabilityThisWeek[1])+"</p><p align=\"center\">("+(currLblty < 0 ? roundOffTo2DecPlaces(-currLblty)+"&darr;" : (roundOffTo2DecPlaces(currLblty)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[1] == null ?0:(Double)liabilityLastWeek[1])+"</td>");
		}else if(pointsLblty == 0 && currLblty != 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[0] == null ?0:((Double)liabilityThisWeek[0]).longValue())+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[0] == null ?0:((Double)liabilityLastWeek[0]).longValue())+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[1] == null ?0:(Double)liabilityThisWeek[1])+"</p><p align=\"center\">("+(currLblty < 0 ? roundOffTo2DecPlaces(-currLblty)+"&darr;" : (roundOffTo2DecPlaces(currLblty)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[1] == null ?0:(Double)liabilityLastWeek[1])+"</td>");
		}else if(pointsLblty != 0 && currLblty == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[0] == null ?0:((Double)liabilityThisWeek[0]).longValue())+"</p><p align=\"center\">("+(pointsLblty < 0 ? roundOffTo2DecPlaces(-pointsLblty)+"&darr;" : (roundOffTo2DecPlaces(pointsLblty)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[0] == null ?0:((Double)liabilityLastWeek[0]).longValue())+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[1] == null ?0:(Double)liabilityThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[1] == null ?0:(Double)liabilityLastWeek[1])+"</td>");
		}else if(pointsLblty == 0 && currLblty == 0){
			strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[0] == null ?0:((Double)liabilityThisWeek[0]).longValue())+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[0] == null ?0:((Double)liabilityLastWeek[0]).longValue())+"</td>"
					+ "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(liabilityThisWeek == null || liabilityThisWeek[1] == null ?0:(Double)liabilityThisWeek[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(liabilityLastWeek == null || liabilityLastWeek[1] == null ?0:(Double)liabilityLastWeek[1])+"</td>");
		}
		strBuff.append("</tr><tr></table>");
		
		return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> weeklyTransactionSummaryValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count1 =loyaltyTransactionChildDao.getEnrollmentsTransactionSummaryWeekly(userId,fromDate,toDate,prgId);
			
			return count1;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	@SuppressWarnings("null")
	private List<Object[]> weeklyTransactionSummaryValues1(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count1 = new ArrayList<Object[]>();
					
					
			count1.addAll(loyaltyTransactionChildDao.getIssuanceAndReturnTransactionSummaryWeekly(userId,fromDate,toDate,prgId));
			
			count1.addAll(loyaltyTransactionChildDao.getReturnCountWeekly(userId,fromDate,toDate,prgId));
			count1.add(loyaltyTransactionChildDao.getLiability(userId, fromDate, toDate, prgId));
			return count1;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private StringBuffer weeklyTierKpiDetailedReportHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
			logger.info("====W3====");
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("<br/><br/><b style=\"margin-left:20px;\">Tier KPIs Detailed Report</b><hr style=\"color:#ffffff;\"><br/>");

			String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);
			List<Object[]> tierDetail = getAllTiers(prgId,userId);
			List<Object[]> str = kpiDetailedTableValues(userId,fromDate,toDate,prgId);
			List<Object[]> str1 = kpiDetailedRevenueValues(userId,fromDate,toDate,prgId);

			from.add(Calendar.DAY_OF_MONTH, -7);
			till.add(Calendar.DAY_OF_MONTH, -7);

			fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			from.add(Calendar.DAY_OF_MONTH, 7);
			till.add(Calendar.DAY_OF_MONTH, 7);

			List<Object[]> str2 = kpiDetailedTableValues(userId,fromDate,toDate,prgId);
			List<Object[]> str3 = kpiDetailedRevenueValues(userId,fromDate,toDate,prgId);

			if((str==null || str.size() == 0) && (str2==null || str2.size() == 0) && (tierDetail == null || tierDetail.size() == 0)){
				strBuff.append("<p>Not Available</p>");
				return strBuff;
			}

			strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
					+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>");
			
			if(tierDetail != null && tierDetail.size() > 0){
				for (Object[] tier : tierDetail) {
					strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">"+tier[0]+"("+tier[1]+")"+"</th>");
				}
			}

			strBuff.append("</tr><tr>");
			
			for (Object[] tier1 : tierDetail) {
				strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This Week</th>"
						+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>");
			}
			
			
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Total Memberships</th>");

			String enrolledStr = Constants.STRING_NILL;
			String updatedStr = Constants.STRING_NILL;

			double value = 0.0;
			for(Object[] tier : tierDetail){
				
				Object[] thisWeekMembershipObjectArray = null;
				Object[] lastWeekMembershipObjectArray = null;
				
				boolean foundThisWeekMembershipOnly = false;
				boolean foundLastWeekMembershipOnly = false;
				
				if(str != null && str.size() > 0){
					for(Object[] thisWeekStore : str){ 
						if(tier[2].toString().equalsIgnoreCase(thisWeekStore[3].toString())){
							foundThisWeekMembershipOnly = true;
							thisWeekMembershipObjectArray = thisWeekStore;
						}
					}
				}
				
				if(str2 != null && str2.size() > 0){
					for(Object[] lastWeekStore : str2){ 
						if(tier[2].toString().equalsIgnoreCase(lastWeekStore[3].toString())){
							foundLastWeekMembershipOnly = true;
							lastWeekMembershipObjectArray = lastWeekStore;
						}
					}
				}
				
				if(foundThisWeekMembershipOnly && foundLastWeekMembershipOnly){
					
					value = (((thisWeekMembershipObjectArray == null || thisWeekMembershipObjectArray[2] == null || (Long)thisWeekMembershipObjectArray[2] == 0 ? 0 :((Long)thisWeekMembershipObjectArray[2]).doubleValue()) 
							- (lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || (Long)lastWeekMembershipObjectArray[2] == 0 ? 0 : ((Long)lastWeekMembershipObjectArray[2])))
							/(lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || ((Long)lastWeekMembershipObjectArray[2]) == 0 ? 1:((Long)lastWeekMembershipObjectArray[2]).doubleValue()))*100;
					if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekMembershipObjectArray[2]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekMembershipObjectArray[2]+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekMembershipObjectArray[2]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekMembershipObjectArray[2]+"</td>");
					}
					
				}else if(foundThisWeekMembershipOnly){
					
					value = (((thisWeekMembershipObjectArray == null || thisWeekMembershipObjectArray[2] == null || (Long)thisWeekMembershipObjectArray[2] == 0 ? 0 :((Long)thisWeekMembershipObjectArray[2]).doubleValue()) 
							- (lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || (Long)lastWeekMembershipObjectArray[2] == 0 ? 0 : ((Long)lastWeekMembershipObjectArray[2])))
							/(lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || ((Long)lastWeekMembershipObjectArray[2]) == 0 ? 1:((Long)lastWeekMembershipObjectArray[2]).doubleValue()))*100;
					if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekMembershipObjectArray[2]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekMembershipObjectArray[2]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					
				}else if(foundLastWeekMembershipOnly){
					
					value = (((thisWeekMembershipObjectArray == null || thisWeekMembershipObjectArray[2] == null || (Long)thisWeekMembershipObjectArray[2] == 0 ? 0 :((Long)thisWeekMembershipObjectArray[2]).doubleValue()) 
							- (lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || (Long)lastWeekMembershipObjectArray[2] == 0 ? 0 : ((Long)lastWeekMembershipObjectArray[2])))
							/(lastWeekMembershipObjectArray == null || lastWeekMembershipObjectArray[2] == null || ((Long)lastWeekMembershipObjectArray[2]) == 0 ? 1:((Long)lastWeekMembershipObjectArray[2]).doubleValue()))*100;
					if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekMembershipObjectArray[2]+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekMembershipObjectArray[2]+"</td>");
					}
					
				}else {
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
			}

			
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits</th>");

			String revenueStr = Constants.STRING_NILL;

			for(Object[] tier : tierDetail){
				
				Object[] thisWeekVisitAndRevenueObjectArray = null;
				Object[] lastWeekVisitAndRevenueObjectArray = null;
				
				boolean foundThisWeekVisitAndRevenueOnly = false;
				boolean foundLastWeekVisitAndRevenueOnly = false;
				
				if(str1 != null && str1.size() > 0){
					for(Object[] thisWeekVisitStore : str1){ 

						if(tier[2].toString().equalsIgnoreCase(thisWeekVisitStore[0].toString())){

							foundThisWeekVisitAndRevenueOnly = true;
							thisWeekVisitAndRevenueObjectArray = thisWeekVisitStore;

						}

					}
				}
				
				if(str3 != null && str3.size() > 0){
					for(Object[] lastWeekVisitStore : str3){ 

						if(tier[2].toString().equalsIgnoreCase(lastWeekVisitStore[0].toString())){

							foundLastWeekVisitAndRevenueOnly = true;
							lastWeekVisitAndRevenueObjectArray = lastWeekVisitStore;

						}

					}
				}
				
				
				if(foundThisWeekVisitAndRevenueOnly && foundLastWeekVisitAndRevenueOnly){
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : (Long)lastWeekVisitAndRevenueObjectArray[1]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					if(value1 != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+lastWeekVisitAndRevenueObjectArray[1]+"</td>");
					}else if(value1 == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+lastWeekVisitAndRevenueObjectArray[1]+"</td>");
					}
					double value2 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)lastWeekVisitAndRevenueObjectArray[2]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 1 : (Double)lastWeekVisitAndRevenueObjectArray[2]))*100;
					if(value2 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+(value2 < 0 ? roundOffTo2DecPlaces(-value2)+"&darr;" : (roundOffTo2DecPlaces(value2)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}else if(value2 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}
					
				}else if(foundThisWeekVisitAndRevenueOnly){
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : (Long)lastWeekVisitAndRevenueObjectArray[1]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					if(value1 != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(value1 == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					double value2 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)lastWeekVisitAndRevenueObjectArray[2]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 1 : (Double)lastWeekVisitAndRevenueObjectArray[2]))*100;
					if(value2 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+(value2 < 0 ? roundOffTo2DecPlaces(-value2)+"&darr;" : (roundOffTo2DecPlaces(value2)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}else if(value2 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}
					
				}else if(foundLastWeekVisitAndRevenueOnly){
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : (Long)lastWeekVisitAndRevenueObjectArray[1]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					if(value1 != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+lastWeekVisitAndRevenueObjectArray[1]+"</td>");
					}else if(value1 == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+lastWeekVisitAndRevenueObjectArray[1]+"</td>");
					}
					double value2 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)lastWeekVisitAndRevenueObjectArray[2]))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 1 : (Double)lastWeekVisitAndRevenueObjectArray[2]))*100;
					if(value2 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value2 < 0 ? roundOffTo2DecPlaces(-value2)+"&darr;" : (roundOffTo2DecPlaces(value2)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}else if(value2 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}
					
				}else{
					
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					
				}
				
				
			}
			
			
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue</th>");
			strBuff.append(revenueStr);
			strBuff.append("</tr></table>");

			return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	
	private StringBuffer weeklyStoreKpiDetailedReportHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
			logger.info("====W4====");
			StringBuffer strBuff = new StringBuffer();

			String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			List<Object[]> storeNumbers = getAllStores(userId,fromDate,toDate,prgId);
			List<Object[]> str = storeKpiDetailedEnrollmentValues(userId,fromDate,toDate,prgId);
			List<Object[]> str1 = kpiDetailedIssuanceValues(userId,fromDate,toDate,prgId);
			List<Object[]> str2 = kpiDetailedVisitsValues(userId,fromDate,toDate,prgId);

			strBuff.append("<br/><br/><b style=\"margin-left:20px;\">Store KPIs Detailed Report</b><hr style=\"color:#ffffff;\"><br/>");

			from.add(Calendar.DAY_OF_MONTH, -7);
			till.add(Calendar.DAY_OF_MONTH, -7);

			fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			from.add(Calendar.DAY_OF_MONTH, 7);
			till.add(Calendar.DAY_OF_MONTH, 7);

			List<Object[]> storeNumbers1 = getAllStores(userId,fromDate,toDate,prgId);
			List<Object[]> str3 = storeKpiDetailedEnrollmentValues(userId,fromDate,toDate,prgId);
			List<Object[]> str4 = kpiDetailedIssuanceValues(userId,fromDate,toDate,prgId);
			List<Object[]> str5 = kpiDetailedVisitsValues(userId,fromDate,toDate,prgId);

			if((storeNumbers == null || storeNumbers.size() == 0) && (storeNumbers1 == null || storeNumbers1.size() == 0)){
				strBuff.append("<p>Not Available</p>");
				return strBuff;
			}

	//		logger.info("(storeNumbers.size() >>>>>>>>>>>>>>> storeNumbers1.size() "+(storeNumbers.size() +","+ storeNumbers1.size()));
			strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
					+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>");
			
			if(storeNumbers != null && storeNumbers.size() > 0){
				for (Object[] store : storeNumbers) {
					int i = 0;
					strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">"+store[i]+"</th>");
				}
			}

			strBuff.append("</tr><tr style=\"background : #444444;color:#ffffff;\">");

			
			for (Object[] store1 : storeNumbers) {
				strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This Week</th>"
						+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>");
			}

			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Enrollments</th>");

	//		logger.info("(str.size() >>>>>>>>>>>>>>> str3.size() "+(str.size() +","+ str3.size()));

			for (Object[] store1 : storeNumbers) {
				
					Object[] lastWeekObjectArray = null;
					Object[] thisWeekObjectArray = null;
					
					boolean foundThisWeekOnly = false;
					boolean foundLastWeekOnly = false;
					
					if(str != null && str.size() > 0){
						for(Object[] thisWeekStore : str){ 
							if(store1[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
								foundThisWeekOnly = true;
								thisWeekObjectArray = thisWeekStore;
							}
						}
					}
					
					if(str3 != null && str3.size() > 0){
						for(Object[] lastWeekStore : str3){ 
							if(store1[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
								foundLastWeekOnly = true;
								lastWeekObjectArray = lastWeekStore;
							}
						}
					}
					
					
					if(foundThisWeekOnly && foundLastWeekOnly){
						double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
								- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
								/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekObjectArray[1]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekObjectArray[1]+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Long)lastWeekObjectArray[1])+"</td>");
					}
					}else if(foundThisWeekOnly){
						double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
								- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
								/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((Long)thisWeekObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(foundLastWeekOnly){
						double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
								- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
								/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Long)lastWeekObjectArray[1])+"</td>");
					}else {
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					
				}
			
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">New Gift-Card Issuances</th>");
		//	logger.info("(str1.size() >>>>>>>>>>>>>>> str4.size() "+(str1.size() +","+ str4.size()));
			int j = 0;
			for (Object[] store1 : storeNumbers) {
				

				
				Object[] lastWeekObjectArray = null;
				Object[] thisWeekObjectArray = null;
				
				boolean foundThisWeekOnly = false;
				boolean foundLastWeekOnly = false;
				
				if(str1 != null && str1.size() > 0){
					for(Object[] thisWeekStore : str1){ 
						if(store1[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
							foundThisWeekOnly = true;
							thisWeekObjectArray = thisWeekStore;
						}
					}
				}
				
				if(str4 != null && str4.size() > 0){
					for(Object[] lastWeekStore : str4){ 
						if(store1[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
							foundLastWeekOnly = true;
							lastWeekObjectArray = lastWeekStore;
						}
					}
				}
				
				
				if(foundThisWeekOnly && foundLastWeekOnly){
					double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
							- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
							/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
					
					if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekObjectArray[1]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekObjectArray[1]+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Long)lastWeekObjectArray[1])+"</td>");
					}
				}else if(foundThisWeekOnly){
					double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
							- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
							/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+((Long)thisWeekObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(foundLastWeekOnly){
					double value = (((thisWeekObjectArray == null || thisWeekObjectArray[1] == null || (Long)thisWeekObjectArray[1] == 0 ? 0 : ((Long)thisWeekObjectArray[1]).doubleValue()) 
							- (lastWeekObjectArray == null || lastWeekObjectArray[1] == null || (Long)lastWeekObjectArray[1] == 0 ? 0 : ((Long)lastWeekObjectArray[1]).doubleValue()))
							/(lastWeekObjectArray == null || lastWeekObjectArray[1] == null || ((Long)lastWeekObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekObjectArray[1]).doubleValue()))*100;
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+((Long)lastWeekObjectArray[1])+"</td>");
				}else {
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
			}
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Visits From Loyalty</th>");

			int k = 0;
			String revenueStr = Constants.STRING_NILL;

	//		logger.info("(str2.size() >>>>>>>>>>>>>>> str5.size() "+(str2.size() +","+ str5.size()));
			
			for (Object[] store1 : storeNumbers) {
				
				boolean foundVisitAndRevenueThisWeekOnly = false;
				boolean foundVisitAndRevenueLastWeekOnly = false;
				
				Object[] thisWeekVisitAndRevenueObjectArray = null;
				Object[] lastWeekVisitAndRevenueObjectArray = null;
				
				if(str2 != null && str2.size() > 0){
					for(Object[] thisWeekStore : str2){
						if(store1[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
							foundVisitAndRevenueThisWeekOnly = true;
							thisWeekVisitAndRevenueObjectArray = thisWeekStore;
						}
					}
				}
				
				if(str5 != null && str5.size() > 0){
					for(Object[] lastWeekStore : str5){
						if(store1[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
							foundVisitAndRevenueLastWeekOnly = true;
							lastWeekVisitAndRevenueObjectArray = lastWeekStore;
						}
					}
				}
				
				if(foundVisitAndRevenueThisWeekOnly && foundVisitAndRevenueLastWeekOnly){
					
					double value = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekVisitAndRevenueObjectArray[1]+"</td>");
						}else if(value == 0){
							strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekVisitAndRevenueObjectArray[1]+"</td>");
						}
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : ((Double)lastWeekVisitAndRevenueObjectArray[2])))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || ((Double)lastWeekVisitAndRevenueObjectArray[2]) == 0 ? 1 : ((Double)lastWeekVisitAndRevenueObjectArray[2])))*100;
					if(value1 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}else if(value1 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}
					
				}else if(foundVisitAndRevenueThisWeekOnly){
					
					double value = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
						}else if(value == 0){
							strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(Long)thisWeekVisitAndRevenueObjectArray[1]+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
						}
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : ((Double)lastWeekVisitAndRevenueObjectArray[2]).doubleValue()))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || ((Double)lastWeekVisitAndRevenueObjectArray[2]).doubleValue() == 0 ? 1 : ((Double)lastWeekVisitAndRevenueObjectArray[2]).doubleValue()))*100;
					if(value1 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.00+"</td>";
					}else if(value1 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+roundOffTo2DecPlaces((Double)thisWeekVisitAndRevenueObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.00+"</td>";
					}
					
				}else if(foundVisitAndRevenueLastWeekOnly){
					
					double value = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[1] == null || (Long)thisWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)thisWeekVisitAndRevenueObjectArray[1]).doubleValue()) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || (Long)lastWeekVisitAndRevenueObjectArray[1] == 0 ? 0 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[1] == null || ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue() == 0 ? 1 : ((Long)lastWeekVisitAndRevenueObjectArray[1]).doubleValue()))*100;
					
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekVisitAndRevenueObjectArray[1]+"</td>");
						}else if(value == 0){
							strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(Long)lastWeekVisitAndRevenueObjectArray[1]+"</td>");
						}
					
					double value1 = (((thisWeekVisitAndRevenueObjectArray == null || thisWeekVisitAndRevenueObjectArray[2] == null || (Double)thisWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : (Double)thisWeekVisitAndRevenueObjectArray[2]) 
							- (lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || (Double)lastWeekVisitAndRevenueObjectArray[2] == 0 ? 0 : ((Double)lastWeekVisitAndRevenueObjectArray[2])))
							/(lastWeekVisitAndRevenueObjectArray == null || lastWeekVisitAndRevenueObjectArray[2] == null || ((Double)lastWeekVisitAndRevenueObjectArray[2]) == 0 ? 1 : ((Double)lastWeekVisitAndRevenueObjectArray[2])))*100;
					if(value1 != 0){
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.00+"</p><p align=\"center\">("+(value1 < 0 ? roundOffTo2DecPlaces(-value1)+"&darr;" : (roundOffTo2DecPlaces(value1)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}else if(value1 == 0){
						revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.00+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+roundOffTo2DecPlaces((Double)lastWeekVisitAndRevenueObjectArray[2])+"</td>";
					}
					
				}else {
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					revenueStr += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.00+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.00+"</td>";
				}
				
			}
			
		
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Revenue From Loyalty</th>");
			strBuff.append(revenueStr);
			strBuff.append("</tr></table>");

			return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private StringBuffer weeklyStoreTranAmtReportHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
			logger.info("====W5====");
			StringBuffer strBuff = new StringBuffer();

			strBuff.append("<br/><br/><b style=\"margin-left:20px;\">Store Transacted Amount Report</b><hr style=\"color:#ffffff;\"><br/>");

			String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			List<Object[]> storeNumbers = getAllStoresForTransaction(userId,fromDate,toDate,prgId);
			List<Object[]> str = storeTrascnAmntValues(userId,fromDate,toDate,prgId);
			List<Object[]> str1 = storeTrascnAmntValues1(userId,fromDate,toDate,prgId);
			List<Object[]> thisWeekLiability = getLiability(userId,fromDate,toDate,prgId);


			from.add(Calendar.DAY_OF_MONTH, -7);
			till.add(Calendar.DAY_OF_MONTH, -7);

			fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
			toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);

			from.add(Calendar.DAY_OF_MONTH, 7);
			till.add(Calendar.DAY_OF_MONTH, 7);

			List<Object[]> storeNumbers1 = getAllStoresForTransaction(userId,fromDate,toDate,prgId);
			List<Object[]> str2 = storeTrascnAmntValues(userId,fromDate,toDate,prgId);
			List<Object[]> str3 = storeTrascnAmntValues1(userId,fromDate,toDate,prgId);
			List<Object[]> lastWeekLiability = getLiability(userId,fromDate,toDate,prgId);

			if((storeNumbers == null || storeNumbers.size() == 0) && (storeNumbers1 == null || storeNumbers1.size() == 0)){
				strBuff.append("<p>Not Available</p>");
				return strBuff;
			}


			strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
					+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>");


			if(storeNumbers != null && storeNumbers.size() > 0){
				for (Object[] store : storeNumbers) {
					int i = 0;
					strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">"+store[i]+"</th>");
				}
			}
			
			strBuff.append("</tr><tr style=\"background : #444444;color:#ffffff;\">");

			if(storeNumbers != null && storeNumbers.size() > 0){
				for (Object[] store1 : storeNumbers) {
					strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This Week</th>"
							+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>");
				}
			}

			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Points Issued</th>");

			String currIssu = Constants.STRING_NILL;
			String giftIssu = Constants.STRING_NILL;
			String pointsRed = Constants.STRING_NILL;
			String currRed = Constants.STRING_NILL;
			String storeCredit = Constants.STRING_NILL;
			String liabilityPoints = Constants.STRING_NILL;
			String liabilityCurrency = Constants.STRING_NILL;
			
			logger.info("===store size are ==="+storeNumbers.size());
			for(Object[] store : storeNumbers){
				
				Object[] thisWeekIssuancePurchaseObjectArray = null;
				Object[] lastWeekIssuancePurchaseObjectArray = null;
				
				
				Object[] thisWeekIssuanceGiftObjectArray = null;
				Object[] lastWeekIssuanceGiftObjectArray = null;
				
				
				Object[] thisWeekRedemptionPointsObjectArray = null;
				Object[] lastWeekRedemptionPointsObjectArray = null;
				
				Object[] thisWeekRedemptionAmountObjectArray = null;
				Object[] lastWeekRedemptionAmountObjectArray = null;
				
				
				
				Object[] thisWeekStoreCreditsObjectArray = null;
				Object[] lastWeekStoreCreditsObjectArray = null;
				
				Object[] thisWeekLiabilityObjectArray = null;
				Object[] lastWeekLiabilityObjectArray = null;
				
				
				
				
				boolean foundThisWeekOnlyTransByIssuanceAndRedmptn = false;
				boolean foundLastWeekOnlyTransByIssuanceAndRedmptn = false;
				
				
				boolean foundThisWeekOnlyStoreCredits = false;
				boolean foundLastWeekOnlyStoreCredits = false;
				
				
				boolean foundThisWeekOnlyLiability = false;
				boolean foundLastWeekOnlyLiability = false;
				
				
				if(str != null && str.size() > 0){

					for(Object[] thisWeekStore : str){ 

						if(store[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
							foundThisWeekOnlyTransByIssuanceAndRedmptn = true;


							if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)){
								thisWeekIssuancePurchaseObjectArray = thisWeekStore;

							}


							if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){
								thisWeekIssuanceGiftObjectArray = thisWeekStore;

							}


							if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
								thisWeekRedemptionAmountObjectArray = thisWeekStore;

							}


							if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
								thisWeekRedemptionPointsObjectArray = thisWeekStore;

							}

						}
					}
				}
				
				if(str2 != null && str2.size() > 0){

					for(Object[] lastWeekStore : str2){ 
						if(store[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
							foundLastWeekOnlyTransByIssuanceAndRedmptn = true;


							if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)){
								lastWeekIssuancePurchaseObjectArray = lastWeekStore;

							}


							if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){
								lastWeekIssuanceGiftObjectArray = lastWeekStore;

							}

							if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
								lastWeekRedemptionAmountObjectArray = lastWeekStore;

							}

							if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
								lastWeekRedemptionPointsObjectArray = lastWeekStore;

							}
						}
					}
				}
				
				if(str1 != null && str1.size() > 0){

					for(Object[] thisWeekStore : str1){ 
						if(store[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
							foundThisWeekOnlyStoreCredits = true;
							thisWeekStoreCreditsObjectArray = thisWeekStore;
						}
					}
				}
				
				if(str3 != null && str3.size() > 0){

					for(Object[] lastWeekStore : str3){ 
						if(store[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
							foundLastWeekOnlyStoreCredits = true;
							lastWeekStoreCreditsObjectArray = lastWeekStore;
						}
					}
				}
				
				if(thisWeekLiability != null && thisWeekLiability.size() > 0){
					
					for(Object[] thisWeekStore : thisWeekLiability){ 
						if(store[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){
							foundThisWeekOnlyLiability = true;
							thisWeekLiabilityObjectArray = thisWeekStore;
						}
					}
				}
				
				if(lastWeekLiability != null && lastWeekLiability.size() > 0){

					for(Object[] lastWeekStore : lastWeekLiability){ 
						if(store[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){
							foundLastWeekOnlyLiability = true;
							lastWeekLiabilityObjectArray = lastWeekStore;
						}
					}
				}
				
				if(foundThisWeekOnlyTransByIssuanceAndRedmptn && foundLastWeekOnlyTransByIssuanceAndRedmptn){
					
					
					double value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : ((Long)thisWeekIssuancePurchaseObjectArray[6]).doubleValue()) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 1 : ((Long)lastWeekIssuancePurchaseObjectArray[6]).doubleValue()))*100;
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)thisWeekIssuancePurchaseObjectArray[6])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6])+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)thisWeekIssuancePurchaseObjectArray[6])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6])+"</td>");
					}
					
					
					
					value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuancePurchaseObjectArray[7]) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuancePurchaseObjectArray[7]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuancePurchaseObjectArray[7]))*100;
					if(value != 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuancePurchaseObjectArray[7])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuancePurchaseObjectArray[7])+"</td>";
					}else if(value == 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuancePurchaseObjectArray[7])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuancePurchaseObjectArray[7])+"</td>";
					}

					value = (((thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuanceGiftObjectArray[7]) 
							- (lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuanceGiftObjectArray[7]))
							/(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuanceGiftObjectArray[7]))*100;
					if(value != 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuanceGiftObjectArray[7])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuanceGiftObjectArray[7])+"</td>";
					}else if(value == 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuanceGiftObjectArray[7])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuanceGiftObjectArray[7])+"</td>";
					}
					value = (((thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)thisWeekRedemptionPointsObjectArray[3]) 
							- (lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)lastWeekRedemptionPointsObjectArray[3]))
							/(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 1 : (Double)lastWeekRedemptionPointsObjectArray[3]))*100;
					if(value != 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)thisWeekRedemptionPointsObjectArray[3]))+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)lastWeekRedemptionPointsObjectArray[3]))+"</td>";
					}else if(value == 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)thisWeekRedemptionPointsObjectArray[3]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)lastWeekRedemptionPointsObjectArray[3]))+"</td>";
					}
					
					value = (((thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)thisWeekRedemptionAmountObjectArray[4])) 
							- (lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))
							/(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 1 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))*100;
					if(value != 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)thisWeekRedemptionAmountObjectArray[4]))+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)lastWeekRedemptionAmountObjectArray[4]))+"</td>";
					}else if(value == 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)thisWeekRedemptionAmountObjectArray[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)lastWeekRedemptionAmountObjectArray[4]))+"</td>";
					}
					
				}else if(foundThisWeekOnlyTransByIssuanceAndRedmptn){
					
					double value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : ((Long)thisWeekIssuancePurchaseObjectArray[6]).doubleValue()) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 1 : ((Long)lastWeekIssuancePurchaseObjectArray[6]).doubleValue()))*100;
					
					logger.info("value >>>>>>>>>"+value);
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)thisWeekIssuancePurchaseObjectArray[6])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)thisWeekIssuancePurchaseObjectArray[6])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					
					
					value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuancePurchaseObjectArray[7]) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuancePurchaseObjectArray[7]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuancePurchaseObjectArray[7]))*100;
					logger.info("value 2 >>>>>>>>>"+value);
					if(value != 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuancePurchaseObjectArray[7])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
					}else if(value == 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuancePurchaseObjectArray[7])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
					}
					
					value = (((thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuanceGiftObjectArray[7]) 
							- (lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuanceGiftObjectArray[7]))
							/(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuanceGiftObjectArray[7]))*100;
					logger.info("value 3 >>>>>>>>>"+value);
					if(value != 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuanceGiftObjectArray[7])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}else if(value == 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)thisWeekIssuanceGiftObjectArray[7])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}
					
					value = (((thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)thisWeekRedemptionPointsObjectArray[3]) 
							- (lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)lastWeekRedemptionPointsObjectArray[3]))
							/(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 1 : (Double)lastWeekRedemptionPointsObjectArray[3]))*100;
					logger.info("value 4 >>>>>>>>>"+value);
					
					if(value != 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)thisWeekRedemptionPointsObjectArray[3]))+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}else if(value == 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)thisWeekRedemptionPointsObjectArray[3]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					}
					
					value = (((thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)thisWeekRedemptionAmountObjectArray[4])) 
							- (lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))
							/(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 1 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))*100;
					
					logger.info("value 5 >>>>>>>>>"+value);
					if(value != 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)thisWeekRedemptionAmountObjectArray[4]))+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
					}else if(value == 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)thisWeekRedemptionAmountObjectArray[4]))+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
					}
				
				}else if(foundLastWeekOnlyTransByIssuanceAndRedmptn){
					
					double value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[6] == null || (Long)thisWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : ((Long)thisWeekIssuancePurchaseObjectArray[6]).doubleValue()) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 1 : ((Long)lastWeekIssuancePurchaseObjectArray[6]).doubleValue()))*100;
					if(value != 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6])+"</td>");
					}else if(value == 0){
						strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[6] == null || (Long)lastWeekIssuancePurchaseObjectArray[6] == 0 ? 0 : (Long)lastWeekIssuancePurchaseObjectArray[6])+"</td>");
					}
					
					
					
					value = (((thisWeekIssuancePurchaseObjectArray == null || thisWeekIssuancePurchaseObjectArray[7] == null || (Double)thisWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuancePurchaseObjectArray[7]) 
							- (lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuancePurchaseObjectArray[7]))
							/(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuancePurchaseObjectArray[7]))*100;
					if(value != 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuancePurchaseObjectArray[7])+"</td>";
					}else if(value == 0){
						currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuancePurchaseObjectArray == null || lastWeekIssuancePurchaseObjectArray[7] == null || (Double)lastWeekIssuancePurchaseObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuancePurchaseObjectArray[7])+"</td>";
					}
				
					value = (((thisWeekIssuanceGiftObjectArray == null || thisWeekIssuanceGiftObjectArray[7] == null || (Double)thisWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)thisWeekIssuanceGiftObjectArray[7]) 
							- (lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0 : (Double)lastWeekIssuanceGiftObjectArray[7]))
							/(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 1 : (Double)lastWeekIssuanceGiftObjectArray[7]))*100;
					if(value != 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuanceGiftObjectArray[7])+"</td>";
					}else if(value == 0){
						giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekIssuanceGiftObjectArray == null || lastWeekIssuanceGiftObjectArray[7] == null || (Double)lastWeekIssuanceGiftObjectArray[7] == 0 ? 0.0 : (Double)lastWeekIssuanceGiftObjectArray[7])+"</td>";
					}
					value = (((thisWeekRedemptionPointsObjectArray == null || thisWeekRedemptionPointsObjectArray[3] == null || (Double)thisWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)thisWeekRedemptionPointsObjectArray[3]) 
							- (lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0 : (Double)lastWeekRedemptionPointsObjectArray[3]))
							/(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 1 : (Double)lastWeekRedemptionPointsObjectArray[3]))*100;
					if(value != 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)lastWeekRedemptionPointsObjectArray[3]))+"</td>";
					}else if(value == 0){
						pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionPointsObjectArray == null || lastWeekRedemptionPointsObjectArray[3] == null || (Double)lastWeekRedemptionPointsObjectArray[3] == 0 ? 0.0 : (-(Double)lastWeekRedemptionPointsObjectArray[3]))+"</td>";
					}
					
					value = (((thisWeekRedemptionAmountObjectArray == null || thisWeekRedemptionAmountObjectArray[4] == null || (Double)thisWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)thisWeekRedemptionAmountObjectArray[4])) 
							- (lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))
							/(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 1 : (-(Double)lastWeekRedemptionAmountObjectArray[4])))*100;
					if(value != 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)lastWeekRedemptionAmountObjectArray[4]))+"</td>";
					}else if(value == 0){
						currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemptionAmountObjectArray == null || lastWeekRedemptionAmountObjectArray[4] == null || (Double)lastWeekRedemptionAmountObjectArray[4] == 0 ? 0.0 : (-(Double)lastWeekRedemptionAmountObjectArray[4]))+"</td>";
					}
					
				}else {
					
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					currIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
					giftIssu += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					pointsRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>";
					currRed += "<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>";
				}
				
				
				if(foundThisWeekOnlyStoreCredits && foundLastWeekOnlyStoreCredits){
					
					Double value = (((thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)thisWeekStoreCreditsObjectArray[1]) 
							- (lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)lastWeekStoreCreditsObjectArray[1]))
							/(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 1 : (Double)lastWeekStoreCreditsObjectArray[1]))*100;
					
					if(value != 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)thisWeekStoreCreditsObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)lastWeekStoreCreditsObjectArray[1])+"</td>");
					}else if(value == 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)thisWeekStoreCreditsObjectArray[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)lastWeekStoreCreditsObjectArray[1])+"</td>");
					}
				}else if(foundThisWeekOnlyStoreCredits){
					
					Double value = (((thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)thisWeekStoreCreditsObjectArray[1]) 
							- (lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)lastWeekStoreCreditsObjectArray[1]))
							/(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 1 : (Double)lastWeekStoreCreditsObjectArray[1]))*100;
					
					if(value != 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)thisWeekStoreCreditsObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(value == 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)thisWeekStoreCreditsObjectArray[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					
				}else if(foundLastWeekOnlyStoreCredits){
					
					Double value = (((thisWeekStoreCreditsObjectArray == null || thisWeekStoreCreditsObjectArray[1] == null || (Double)thisWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)thisWeekStoreCreditsObjectArray[1]) 
							- (lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0 : (Double)lastWeekStoreCreditsObjectArray[1]))
							/(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 1 : (Double)lastWeekStoreCreditsObjectArray[1]))*100;
					
					if(value != 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)lastWeekStoreCreditsObjectArray[1])+"</td>");
					}else if(value == 0){
						storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCreditsObjectArray == null || lastWeekStoreCreditsObjectArray[1] == null || (Double)lastWeekStoreCreditsObjectArray[1] == 0 ? 0.0 : (Double)lastWeekStoreCreditsObjectArray[1])+"</td>");
					}
					
				}else {
					storeCredit += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
				
				if(foundThisWeekOnlyLiability && foundLastWeekOnlyLiability){
					
					double value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : ((Long)thisWeekLiabilityObjectArray[1]).doubleValue()) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 1 : ((Long)lastWeekLiabilityObjectArray[1]).doubleValue()))*100;
					if(value != 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)thisWeekLiabilityObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1])+"</td>");
					}else if(value == 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)thisWeekLiabilityObjectArray[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1])+"</td>");
					}
					
					value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)thisWeekLiabilityObjectArray[2]) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)lastWeekLiabilityObjectArray[2]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 1 : (Double)lastWeekLiabilityObjectArray[2]))*100;
					if(value != 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)thisWeekLiabilityObjectArray[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)lastWeekLiabilityObjectArray[2])+"</td>");
					}else if(value == 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)thisWeekLiabilityObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)lastWeekLiabilityObjectArray[2])+"</td>");
					}
					
				}else if(foundThisWeekOnlyLiability){
					
					double value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : ((Long)thisWeekLiabilityObjectArray[1]).doubleValue()) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 1 : ((Long)lastWeekLiabilityObjectArray[1]).doubleValue()))*100;
					if(value != 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)thisWeekLiabilityObjectArray[1])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}else if(value == 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)thisWeekLiabilityObjectArray[1])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					}
					
					value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)thisWeekLiabilityObjectArray[2]) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)lastWeekLiabilityObjectArray[2]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 1 : (Double)lastWeekLiabilityObjectArray[2]))*100;
					if(value != 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)thisWeekLiabilityObjectArray[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
					}else if(value == 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)thisWeekLiabilityObjectArray[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
					}
					
				}else if(foundLastWeekOnlyLiability){
					
					double value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[1] == null || (Long)thisWeekLiabilityObjectArray[1] == 0 ? 0 : ((Long)thisWeekLiabilityObjectArray[1]).doubleValue()) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 1 : ((Long)lastWeekLiabilityObjectArray[1]).doubleValue()))*100;
					if(value != 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1])+"</td>");
					}else if(value == 0){
						liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[1] == null || (Long)lastWeekLiabilityObjectArray[1] == 0 ? 0 : (Long)lastWeekLiabilityObjectArray[1])+"</td>");
					}
					
					value = (((thisWeekLiabilityObjectArray == null || thisWeekLiabilityObjectArray[2] == null || (Double)thisWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)thisWeekLiabilityObjectArray[2]) 
							- (lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0 : (Double)lastWeekLiabilityObjectArray[2]))
							/(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 1 : (Double)lastWeekLiabilityObjectArray[2]))*100;
					if(value != 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)lastWeekLiabilityObjectArray[2])+"</td>");
					}else if(value == 0){
						liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLiabilityObjectArray == null || lastWeekLiabilityObjectArray[2] == null || (Double)lastWeekLiabilityObjectArray[2] == 0 ? 0.0 : (Double)lastWeekLiabilityObjectArray[2])+"</td>");
					}
					
				}else {
					liabilityPoints += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
					liabilityCurrency += ("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0.0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0.0+"</td>");
				}
				
			}
			
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Currency Issued</th>");
			strBuff.append(currIssu);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issued</th>");
			strBuff.append(giftIssu);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Points Redeemed</th>");
			strBuff.append(pointsRed);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Currency Redeemed</th>");
			strBuff.append(currRed);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credits</th>");
			strBuff.append(storeCredit);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability Points</th>");	
			strBuff.append(liabilityPoints);
			strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Liability Currency</th>");
			strBuff.append(liabilityCurrency);

			strBuff.append("</tr></table>");

			return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	
	private List<Object[]> storeTrascnAmntValues(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count =loyaltyTransactionChildDao.getTrnscAmntByStore(userId,fromDate,toDate,prgId);
			
			return count;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	
	private List<Object[]> storeTrascnAmntValues1(Long userId, String fromDate,String toDate, Long prgId) {
		try {
			List<Object[]> count =loyaltyTransactionChildDao.getStoreCreditByStore(userId,fromDate,toDate,prgId);
			
			return count;
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	
	private List getLiability(Long userId, String fromDate,String toDate, Long prgId) {
		
		try {
			return loyaltyTransactionChildDao.getLiabilityByStore(userId,fromDate,toDate,prgId);
			// liability[1] =loyaltyTransactionChildDao.getCurrencyLiability(userId,prgId,fromDate,toDate);
			
			}catch (Exception e) {		
			logger.error("Exception ::",e);
		}
		return new ArrayList(0);
	}
	
	private StringBuffer weeklyStoreTransactionReportHtml(Long userId, Calendar from, Calendar till, Long prgId){
		try{
		logger.info("====W6====");
		StringBuffer strBuff = new StringBuffer();
		
		strBuff.append("<br/><br/><b style=\"margin-left:20px;\">Store Transactions Report</b><hr style=\"color:#ffffff;\"><br/>");
		
		String fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
		String toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		List<Object[]> storeNumbers = getAllStoresForTransaction(userId,fromDate,toDate,prgId);
		List<Object[]> str = storeTransactionDetailedValues(userId,fromDate,toDate,prgId);
		List<Object[]> str1 = storeTransactionDetailedValues1(userId,fromDate,toDate,prgId);
		
		
		from.add(Calendar.DAY_OF_MONTH, -7);
		till.add(Calendar.DAY_OF_MONTH, -7);
		
		fromDate =MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATETIME_STYEAR);
		toDate =MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		from.add(Calendar.DAY_OF_MONTH, 7);
		till.add(Calendar.DAY_OF_MONTH, 7);
		
		List<Object[]> storeNumbers1 = getAllStoresForTransaction(userId,fromDate,toDate,prgId);
		List<Object[]> str2 = storeTransactionDetailedValues(userId,fromDate,toDate,prgId);
		List<Object[]> str3 = storeTransactionDetailedValues1(userId,fromDate,toDate,prgId);
		
		if(storeNumbers.size() == 0){
			strBuff.append("<p>Not Available</p>");
			return strBuff;
		}
		
		strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
				+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>");
		strBuff.append("<table style=\"width:90%; display: inline; margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\">"
				+ "<th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\" rowspan=\"2\"></th>");


		if(storeNumbers != null && storeNumbers.size() > 0){
			for (Object[] store : storeNumbers) {
				int i = 0;
				strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">"+store[i]+"</th>");
			}
		}

		strBuff.append("</tr><tr style=\"background : #444444;color:#ffffff;\">");

		if(storeNumbers != null && storeNumbers.size() > 0){
			for (Object[] store1 : storeNumbers) {
				strBuff.append("<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">This Week</th>"
						+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Last Week</th>");
			}
		}
		
		strBuff.append("</tr><tr><th style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enrollment</th>");
		
		StringBuffer redeBuffer = new StringBuffer();
		redeBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redemption</td>");
		StringBuffer ltyIssuBuffer = new StringBuffer();
		ltyIssuBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Loyalty Issuance</td>");
		StringBuffer giftIssuBuffer = new StringBuffer();
		giftIssuBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Gift Issuance</td>");
		StringBuffer storeCreditBuffer = new StringBuffer();
		storeCreditBuffer.append("<tr><td style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Store Credit</td>");
		//	int i = 0;	
		
		for (Object[] store : storeNumbers) {
			
			
			boolean foundThisWeekOnlyTransByEnrollmentAndRedmptn = false;
			boolean foundLastWeekOnlyTransByEnrollmentAndRedmptn = false;
			
			boolean foundThisWeekIssuanceAndStoreCredits = false;
			boolean foundLastWeekIssuanceAndStoreCredits = false;
			
			Object [] thisWeekEnrollment = null;
			Object [] thisWeekRedemption = null;
			
			Object [] lastWeekEnrollment = null;
			Object [] lastWeekRedemption = null;
			
			Object [] thisWeekLtyIssuance = null;
			Object [] lastWeekLtyIssuance = null;
			
			Object [] thisWeekGiftIssuance = null;
			Object [] lastWeekGiftIssuance = null;
			
			Object [] thisWeekStoreCredit = null;
			Object [] lastWeekStoreCredit = null;
			
			if(str != null && str.size() > 0){

				for(Object[] thisWeekStore : str){ 

					if(store[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){

						foundThisWeekOnlyTransByEnrollmentAndRedmptn = true;

						if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){

							thisWeekEnrollment = thisWeekStore;
						}

						if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){

							thisWeekRedemption = thisWeekStore;
						}

					}

				}
			}
			
			if(str2 != null && str2.size() > 0){
				for(Object [] lastWeekStore : str2){

					if(store[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){

						foundLastWeekOnlyTransByEnrollmentAndRedmptn = true;

						if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){

							lastWeekEnrollment = lastWeekStore;
						}

						if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){

							lastWeekRedemption = lastWeekStore;
						}
					}
				}
			}
			
			if(str1 != null && str1.size() > 0){
				for(Object [] thisWeekStore : str1){

					if(store[0].toString().equalsIgnoreCase(thisWeekStore[0].toString())){

						foundThisWeekIssuanceAndStoreCredits = true;

						if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)){

							thisWeekLtyIssuance = thisWeekStore;
						}

						if(thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){

							thisWeekGiftIssuance = thisWeekStore;
						}

						if((thisWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)) && (thisWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT))){

							thisWeekStoreCredit = thisWeekStore;
						}

					}
				}
			}
			
			if(str3 != null && str3.size() > 0){
				for(Object [] lastWeekStore : str3){

					if(store[0].toString().equalsIgnoreCase(lastWeekStore[0].toString())){

						foundLastWeekIssuanceAndStoreCredits = true;

						if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)){

							lastWeekLtyIssuance = lastWeekStore;
						}

						if(lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) && lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT)){

							lastWeekGiftIssuance = lastWeekStore;
						}

						if((lastWeekStore[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)) && (lastWeekStore[2].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT))){

							lastWeekStoreCredit = lastWeekStore;
						}

					}
				}
			}
			
			
			if(foundThisWeekOnlyTransByEnrollmentAndRedmptn && foundLastWeekOnlyTransByEnrollmentAndRedmptn){
				
				double value = (((thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : ((Long)thisWeekEnrollment[2]).doubleValue()) 
						- (lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2]))
						/(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 1 : ((Long)lastWeekEnrollment[2]).doubleValue()))*100;
				if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : (Long)thisWeekEnrollment[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2])+"</td>");
				}else if(value == 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : (Long)thisWeekEnrollment[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2])+"</td>");
				}
				
				value = (((thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : (Long)thisWeekRedemption[2]) 
						- (lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2]))
						/(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 1 : (Long)lastWeekRedemption[2]))*100;
				if(value != 0){
				redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : (Long)thisWeekRedemption[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2])+"</td>");
				}else if(value == 0){
					redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : (Long)thisWeekRedemption[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2])+"</td>");
				}
				
			}else if(foundThisWeekOnlyTransByEnrollmentAndRedmptn){
				
				double value = (((thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : ((Long)thisWeekEnrollment[2]).doubleValue()) 
						- (lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2]))
						/(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 1 : ((Long)lastWeekEnrollment[2]).doubleValue()))*100;
				if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : (Long)thisWeekEnrollment[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(value == 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekEnrollment == null || thisWeekEnrollment[2] == null || (Long)thisWeekEnrollment[2] == 0 ? 0 : (Long)thisWeekEnrollment[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
				value = (((thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : ((Long)thisWeekRedemption[2]).doubleValue()) 
						- (lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2]))
						/(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 1 : ((Long)lastWeekRedemption[2]).doubleValue()))*100;
				if(value != 0){
				redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : (Long)thisWeekRedemption[2])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(value == 0){
					redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : (Long)thisWeekRedemption[2])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
			}else if(foundLastWeekOnlyTransByEnrollmentAndRedmptn){
			
				double value = (((thisWeekEnrollment == null || thisWeekEnrollment[2] == null|| (Long)thisWeekEnrollment[2] == 0 ? 0 : ((Long)thisWeekEnrollment[2]).doubleValue()) 
						- (lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2]))
						/(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 1 : ((Long)lastWeekEnrollment[2]).doubleValue()))*100;
				if(value != 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2])+"</td>");
				}else if(value == 0){
					strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekEnrollment == null || lastWeekEnrollment[2] == null || (Long)lastWeekEnrollment[2] == 0 ? 0 : (Long)lastWeekEnrollment[2])+"</td>");
				}
				
				value = (((thisWeekRedemption == null || thisWeekRedemption[2] == null || (Long)thisWeekRedemption[2] == 0 ? 0 : ((Long)thisWeekRedemption[2]).doubleValue()) 
						- (lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2]))
						/(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 1 : ((Long)lastWeekRedemption[2]).doubleValue()))*100;
				if(value != 0){
				redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2])+"</td>");
				}else if(value == 0){
					redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekRedemption == null || lastWeekRedemption[2] == null || (Long)lastWeekRedemption[2] == 0 ? 0 : (Long)lastWeekRedemption[2])+"</td>");
				}
			}else {
				
				strBuff.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				redeBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
			}
			
			
			if(foundThisWeekIssuanceAndStoreCredits && foundLastWeekIssuanceAndStoreCredits){
				
				double value = (((thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : ((Long)thisWeekLtyIssuance[3]).doubleValue()) 
						- (lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3]))
						/(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 1 : ((Long)lastWeekLtyIssuance[3]).doubleValue()))*100;
				if(value != 0){
				ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : (Long)thisWeekLtyIssuance[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3])+"</td>");
				}else if(value == 0){
					ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : (Long)thisWeekLtyIssuance[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3])+"</td>");
				}
				
				value = (((thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : ((Long)thisWeekGiftIssuance[3]).doubleValue())
						- (lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3]))
						/(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 1 : ((Long)lastWeekGiftIssuance[3]).doubleValue()))*100;
				if(value != 0){
				giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : (Long)thisWeekGiftIssuance[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3])+"</td>");
				}else if(value == 0){
					giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : (Long)thisWeekGiftIssuance[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3])+"</td>");
				}
				
				value = (((thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : ((Long)thisWeekStoreCredit[3]).doubleValue())
						- (lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3]))
						/(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 1 : ((Long)lastWeekStoreCredit[3]).doubleValue()))*100;
				if(value != 0){
				storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : (Long)thisWeekStoreCredit[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3])+"</td>");
				}else if(value == 0){
					storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : (Long)thisWeekStoreCredit[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3])+"</td>");
				}
				
			}else if(foundThisWeekIssuanceAndStoreCredits){
				
				double value = (((thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : ((Long)thisWeekLtyIssuance[3]).doubleValue()) 
						- (lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3]))
						/(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 1 : ((Long)lastWeekLtyIssuance[3]).doubleValue()))*100;
				if(value != 0){
				ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : (Long)thisWeekLtyIssuance[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(value == 0){
					ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : (Long)thisWeekLtyIssuance[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
				value = (((thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : ((Long)thisWeekGiftIssuance[3]).doubleValue())
						- (lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3]))
						/(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 1 : ((Long)lastWeekGiftIssuance[3]).doubleValue()))*100;
				if(value != 0){
				giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : (Long)thisWeekGiftIssuance[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(value == 0){
					giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : (Long)thisWeekGiftIssuance[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
				
				value = (((thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : ((Long)thisWeekStoreCredit[3]).doubleValue())
						- (lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3]))
						/(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 1 : ((Long)lastWeekStoreCredit[3]).doubleValue()))*100;
				if(value != 0){
				storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : (Long)thisWeekStoreCredit[3])+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}else if(value == 0){
					storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+(thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : (Long)thisWeekStoreCredit[3])+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				}
			}else if(foundLastWeekIssuanceAndStoreCredits){
				
				double value = (((thisWeekLtyIssuance == null || thisWeekLtyIssuance[3] == null || (Long)thisWeekLtyIssuance[3] == 0 ? 0 : ((Long)thisWeekLtyIssuance[3]).doubleValue()) 
						- (lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3]))
						/(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 1 : ((Long)lastWeekLtyIssuance[3]).doubleValue()))*100;
				if(value != 0){
				ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3])+"</td>");
				}else if(value == 0){
					ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekLtyIssuance == null || lastWeekLtyIssuance[3] == null || (Long)lastWeekLtyIssuance[3] == 0 ? 0 : (Long)lastWeekLtyIssuance[3])+"</td>");
				}
				
				value = (((thisWeekGiftIssuance == null || thisWeekGiftIssuance[3] == null || (Long)thisWeekGiftIssuance[3] == 0 ? 0 : ((Long)thisWeekGiftIssuance[3]).doubleValue())
						- (lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3]))
						/(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 1 : ((Long)lastWeekGiftIssuance[3]).doubleValue()))*100;
				if(value != 0){
				giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3])+"</td>");
				}else if(value == 0){
					giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekGiftIssuance == null || lastWeekGiftIssuance[3] == null || (Long)lastWeekGiftIssuance[3] == 0 ? 0 : (Long)lastWeekGiftIssuance[3])+"</td>");
				}
				
				value = (((thisWeekStoreCredit == null || thisWeekStoreCredit[3] == null || (Long)thisWeekStoreCredit[3] == 0 ? 0 : ((Long)thisWeekStoreCredit[3]).doubleValue())
						- (lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3]))
						/(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 1 : ((Long)lastWeekStoreCredit[3]).doubleValue()))*100;
				if(value != 0){
				storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+(value < 0 ? roundOffTo2DecPlaces(-value)+"&darr;" : (roundOffTo2DecPlaces(value)+"&uarr;"))+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3])+"</td>");
				}else if(value == 0){
					storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+(lastWeekStoreCredit == null || lastWeekStoreCredit[3] == null || (Long)lastWeekStoreCredit[3] == 0 ? 0 : (Long)lastWeekStoreCredit[3])+"</td>");
				}
			}else {
				
				ltyIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				giftIssuBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
				storeCreditBuffer.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\"><p align=\"center\">"+0+"</p><p align=\"center\">("+0+" % )</p></td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+0+"</td>");
			}
			
		}
		
			strBuff.append("</tr>");
			redeBuffer.append("</tr>");
			ltyIssuBuffer.append("</tr>");
			giftIssuBuffer.append("</tr>");
			storeCreditBuffer.append("</tr>");
			
			strBuff.append(ltyIssuBuffer);
			strBuff.append(giftIssuBuffer);
			strBuff.append(redeBuffer);
			strBuff.append(storeCreditBuffer);

		
		
		strBuff.append("</table>");
		
		return strBuff;
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return null;
	}
	
	private void sendAlertDaily(LoyaltyProgram loyaltyProgramObj, String htmlContent,Calendar cal,UserEmailAlert userEmailAlertObj){
		try{
			logger.info("====inside sendAlertDaily====");
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(loyaltyProgramObj.getUserId());
			String type = OCConstants.LTY_SETTING_REPORT_TYPE;
			String status = Constants.MAIL_SENT_STATUS_ACTIVE;
			String date = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY); 
			
			String emailSubject =null;
			String subject = PropertyUtil.getPropertyValueFromDB("dailyLtyEmailReportSub");
			emailSubject = subject.replace("<fromtodate_daily>", date).replace("<program_name_daily>",loyaltyProgramObj.getProgramName());
			
			htmlContent = htmlContent.replace("<user_first_name>",user.getFirstName());
			htmlContent = htmlContent.replace("<program_name_daily>",loyaltyProgramObj.getProgramName());
			htmlContent = htmlContent.replace("<daily_date>",MyCalendar.calendarToString(cal, MyCalendar.FORMAT_FULLMONTHDATEONLY));
			htmlContent = htmlContent.replace("<username>",user.getUserName());
			htmlContent = htmlContent.replace("<org_id>",Utility.getOnlyOrgId(user.getUserName()));
			
			String strArr[] = userEmailAlertObj.getEmailId().split(Constants.ADDR_COL_DELIMETER);
			for (String arrStr : strArr) {
				logger.info("====Email Queue loop====");
			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue alertEmailQueue = new EmailQueue(emailSubject, htmlContent, type, status, arrStr, new Date(), user);
			//emailQueueDao.saveOrUpdate(alertEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
			
			
			}
		
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}
	
	private void sendAlertWeekly(LoyaltyProgram loyaltyProgramObj, String htmlContent,Calendar from,Calendar till,UserEmailAlert userEmailAlertObj){
		
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(loyaltyProgramObj.getUserId());
			String type = OCConstants.LTY_SETTING_REPORT_TYPE;
			String status = Constants.MAIL_SENT_STATUS_ACTIVE;
			
			String fromtoDate = "";
			fromtoDate = MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATE_YEAR) + " to " + MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATE_YEAR);
			
			String emailSubject =null;
			String subject = PropertyUtil.getPropertyValueFromDB("weeklyLtyEmailReportSub");
			emailSubject = subject.replace("<fromtodate_weekly>", fromtoDate).replace("<program_name_weekly>",loyaltyProgramObj.getProgramName());
			
			htmlContent = htmlContent.replace("<user_first_name>",user.getFirstName());
			htmlContent = htmlContent.replace("<program_name_weekly>",loyaltyProgramObj.getProgramName());
			htmlContent = htmlContent.replace("<weekly_date>",MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATE_YEAR) + " and " + MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATE_YEAR));
			htmlContent = htmlContent.replace("<username>",user.getUserName());
			htmlContent = htmlContent.replace("<org_id>",Utility.getOnlyOrgId(user.getUserName()));
			
			String strArr[] = userEmailAlertObj.getEmailId().split(Constants.ADDR_COL_DELIMETER);
			for (String arrStr : strArr) {
			
			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue alertEmailQueue = new EmailQueue(emailSubject, htmlContent, type, status, arrStr, new Date(), user);
			//emailQueueDao.saveOrUpdate(alertEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
			}
			
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
		
	}
	

	String roundOffTo2DecPlaces(double val) {
		return String.format("%.2f", val);
	}
public String getDateCriteria(String dateRule ,Users users){
	logger.info("=======inside getDateCriteria===");
	 String dateCondition=null;
		SimpleDateFormat format1 = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
	 if(dateRule!=null){
		 Long timezone=Long.parseLong(users.getClientTimeZone());
	    	String fromdt=null;
	    	String todt=null;
	    	String dt[]=dateRule.split("\\|");
	    	int day=0;
	    	if(dt[0].trim().equalsIgnoreCase("withinlast")){
	    	day=(dt[1].trim().equalsIgnoreCase("day"))?Integer.parseInt(dt[2]):(dt[1].trim().equalsIgnoreCase("week"))?(Integer.parseInt(dt[2])*7):Integer.parseInt(dt[2])*30;
	    	endDate = new MyCalendar();
			endDate.set(MyCalendar.HOUR_OF_DAY, 23);
			endDate.set(MyCalendar.MINUTE, 59);
			endDate.set(MyCalendar.SECOND, 59);

			startDate = new MyCalendar();
			startDate.set(MyCalendar.HOUR_OF_DAY, 00);
			startDate.set(MyCalendar.MINUTE, 00);
			startDate.set(MyCalendar.SECOND, 00);

	    	startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - day);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
			todt = "'"+format1.format(endDate.getTime())+"'";
			 fromdt = "'"+format1.format(startDate.getTime())+"'";
			 logger.info("client Time zone "+users.getClientTimeZone());
			 dateCondition=" AND  created_date between "+fromdt+" AND "+todt;
		 	}
	    	else{
	    		fromdt="'"+dt[2]+"'";
	    	    todt="'"+dt[3]+"'";
	    	    dateCondition=" AND  date(date_add(created_date, interval  "+timezone+"  minute)) between "+fromdt+" AND "+todt;
	    		
	    	}
	    	
	 } 
	 return dateCondition;
}
public String getHavingCriteria(String trxRule){
	logger.info("===========inside getHavingCriteria==========");
	 String havingCondition=null;
	 if(trxRule!=null){
	    	String trx[]=trxRule.split("\\|"); 
	    	String sign=(trx[2].trim().equalsIgnoreCase("morethan")) ? ">" :"=";
	    	
	    	if(trx[0].trim().equalsIgnoreCase("trx")){
	    	if(trx[1].trim().equalsIgnoreCase("Issuance"))	
	    		havingCondition=" having issuance_count "+sign+" "+trx[3];
	    	else if(trx[1].trim().equalsIgnoreCase("Redemption"))
	    		havingCondition=" having redemption_count "+sign+" "+trx[3];
	    	else
	    		havingCondition=" having (issuance_count "+sign+" "+trx[3]+" OR redemption_count"+sign+" "+trx[3]+")";
	    	}
	    	else{
	    		 if(trx[0].trim().equalsIgnoreCase("amount")){
	    		    if(trx[1].trim().equalsIgnoreCase("Issuance"))	
		    		  havingCondition=" having issuance_amt "+sign+" "+trx[3];
		    	   else if(trx[1].trim().equalsIgnoreCase("Redemption"))
		    		   havingCondition=" having redemption_amt "+sign+" "+trx[3];
		    	   else
		    		   havingCondition=" having (issuance_amt "+sign+" "+trx[3]+" OR redemption_amt "+sign+" "+trx[3]+")";
	    		 }
	    		 else{
	    			 if(trx[1].trim().equalsIgnoreCase("Issuance"))	
			    		  havingCondition=" having issuance_point "+sign+" "+trx[3];
	    			 else
	    				 havingCondition=" having (issuance_point "+sign+" "+trx[3]+" OR redemption_amt "+sign+" "+trx[3]+")";
	    		 }
	    	}
	    } 
	 return havingCondition;
}
private void sendFraudAlertDaily( String htmlContent, Calendar cal,
		LoyaltyFraudAlert frdalert) {
	try {
		logger.info("====inside sendFraudAlertDaily====");
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUserId(frdalert.getCreatedByUserId());
		String type = OCConstants.LOYALTY_FRAUD_ALERT_REPORT;
		String status = Constants.MAIL_SENT_STATUS_ACTIVE;
		String date = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY);

		String emailSubject = null;
		String subject = PropertyUtil.getPropertyValueFromDB("loyaltyFraudAlertSub");
		emailSubject = subject.replace("[fromtodate_daily]", date).replace("[alert_rule_name]",frdalert.getRuleName()).replace("[username]",user.getUserName());
		String strArr[] = frdalert.getEmailId().split(Constants.ADDR_COL_DELIMETER);
		logger.info("=========sendFraudAlertDaily  daily email id"+Arrays.toString(strArr));
		for (String arrStr : strArr) {
			logger.info("====Email Queue loop====");
			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue alertEmailQueue = new EmailQueue(emailSubject, htmlContent, type, status, arrStr, new Date(),
					user);
			// emailQueueDao.saveOrUpdate(alertEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
			logger.info("sendFraudAlertDaily is save into Queue");
			frdalert.setLastSentOn(Calendar.getInstance());
			loyaltyfrdAlertDaoForDML.saveOrUpdate(frdalert);
		}

	} catch (Exception e) {
		logger.error("Exception in sending alerts", e);
	}
}

private void sendFraudAlertWeekly(String htmlContent,
		 Calendar from, Calendar till,
		 LoyaltyFraudAlert frdalert) {
   logger.info("============inside sendFraudAlertWeekly============");
	try {
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUserId(frdalert.getCreatedByUserId());
		String type = OCConstants.LOYALTY_FRAUD_ALERT_REPORT;
		String status = Constants.MAIL_SENT_STATUS_ACTIVE;

		String fromtoDate = "";
		fromtoDate = MyCalendar.calendarToString(from, MyCalendar.FORMAT_DATE_YEAR) + " to "
				+ MyCalendar.calendarToString(till, MyCalendar.FORMAT_DATE_YEAR);

		String emailSubject = null;
		String subject = PropertyUtil.getPropertyValueFromDB("loyaltyFraudAlertSub");
		emailSubject = subject.replace("[fromtodate_daily]", fromtoDate).replace("[alert_rule_name]",frdalert.getRuleName()).replace("[username]",user.getUserName());

		String strArr[] = frdalert.getEmailId().split(Constants.ADDR_COL_DELIMETER);
		logger.info("sendFraudAlertWeekly Email id "+Arrays.toString(strArr));
		for (String arrStr : strArr) {

			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue alertEmailQueue = new EmailQueue(emailSubject, htmlContent, type, status, arrStr, new Date(),
					user);
			// emailQueueDao.saveOrUpdate(alertEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
			logger.info("===========sendFraudAlertWeekly email is save into queue");
			frdalert.setLastSentOn(Calendar.getInstance());
			loyaltyfrdAlertDaoForDML.saveOrUpdate(frdalert);
		}

	} catch (Exception e) {
		logger.error("Exception in sending alerts", e);
	}

}
public String getTrxRuleForDisplay(String trxRule){
	 StringBuilder sb=new StringBuilder();
	 if(trxRule!=null){
	    	String trx[]=trxRule.split("\\|"); 
	    	String sign=(trx[2].trim().equalsIgnoreCase("morethan")) ? "more than " :"equal to ";
	    	
	    	if(trx[0].trim().equalsIgnoreCase("trx")){
	    	if(trx[1].trim().equalsIgnoreCase("Issuance"))	
	    		sb.append("# of transaction for Issuance  is "+sign+""+trx[3]);
	    	else if(trx[1].trim().equalsIgnoreCase("Redemption"))
	    		sb.append("# of transaction for Redemption  is "+sign+""+trx[3]);
	    	else
	    		sb.append("# of transaction for Issuance & Redemption  are  "+sign+""+trx[3]);
	    	}
	    	else{
	    		 if(trx[0].trim().equalsIgnoreCase("amount")){
	    		    if(trx[1].trim().equalsIgnoreCase("Issuance"))	
	    		    	sb.append("Amount for Issuance  is "+sign+""+trx[3]);
		    	   else if(trx[1].trim().equalsIgnoreCase("Redemption"))
		    		   sb.append("Amount for Redemption  is "+sign+""+trx[3]);
		    	   else
		    		   sb.append("Amount for Issuance & Redemption  are "+sign+""+trx[3]);
	    		 }
	    		 else{
	    			 if(trx[1].trim().equalsIgnoreCase("Issuance"))	
	    				 sb.append("Points for Issuance  is "+sign+""+trx[3]);
	    			 else
	    				 sb.append("Points for Issuance & Redemption  are "+sign+""+trx[3]);
	    		 }
	    	}
	    } 
	 return sb.toString();
}
public String getDateRuleForDisplay(String dateRule){	
	 StringBuilder sb=new StringBuilder();
	      if(dateRule!=null){
		
	    	String dt[]=dateRule.split("\\|");
	    	String day=null;
	    	if(dt[0].trim().equalsIgnoreCase("withinlast")){
	    		if(Integer.parseInt(dt[2])==1)
	    		day=dt[1].equalsIgnoreCase("day")?"day":dt[1].equalsIgnoreCase("week")?"week":"month";
	    		else
	    			day=dt[1].equalsIgnoreCase("day")?"days":dt[1].equalsIgnoreCase("week")?"weeks":"months";
	    	 sb.append("Last "+dt[2]+" "+day);
	    	}
	    	else{
	    	sb.append("From "+dt[2]+" To "+dt[3]);
	    	}
	    	
	 } 
	 return sb.toString();
}
}
