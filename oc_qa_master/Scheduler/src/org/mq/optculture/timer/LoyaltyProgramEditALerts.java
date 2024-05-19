package org.mq.optculture.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.beans.LtySettingsActivityLogs;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.LoyaltyProgramService;
import org.mq.optculture.data.dao.LtySettingsActivityLogsDao;
import org.mq.optculture.data.dao.LtySettingsActivityLogsDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyProgramEditALerts extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	boolean sendAlert = false;

	public void run() {
		logger.info(">>> Started LoyaltyProgramEditALerts timer >>>");
		try {
			List<LtySettingsActivityLogs> listOfLogs = fetchLogs();
			if(listOfLogs != null){
				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
				Long processedPrgmId = null;
				for (LtySettingsActivityLogs logsObj : listOfLogs) {
					sendAlert = false;
					if(processedPrgmId == null || processedPrgmId.longValue() != logsObj.getProgramId().longValue()){
						processedPrgmId = logsObj.getProgramId();
					}
					else if(processedPrgmId.longValue() == logsObj.getProgramId().longValue()){
						continue;
					}
					//to check if program is active
					LoyaltyProgram prgmObj = ltyPrgmService.getProgmObj(processedPrgmId);
					/*if(!OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(prgmObj.getStatus())){
						continue;
					}*/

					List<LtySettingsActivityLogs> tierLogsList = null;
					List<LtySettingsActivityLogs> otpLogList = null;
					List<LtySettingsActivityLogs> bonusLogList = null;
					List<LtySettingsActivityLogs> ltyRewardExpLogList = null;
					List<LtySettingsActivityLogs> giftAmtExpLogList = null;
					List<LtySettingsActivityLogs> ltyMemExpLogList = null;
					List<LtySettingsActivityLogs> giftCardExpLogList = null;
					for (LtySettingsActivityLogs ltySettingsActivityLogs : listOfLogs) {
						if(ltySettingsActivityLogs.getProgramId().longValue() == processedPrgmId.longValue()){
							if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_PROGRAM)){
								if(otpLogList == null){
									otpLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								otpLogList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_TIER)){
								if(tierLogsList == null){
									tierLogsList = new ArrayList<LtySettingsActivityLogs>();
								}
								tierLogsList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_BONUS)){
								if(bonusLogList == null){
									bonusLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								bonusLogList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_LOYALTY_REWARD_VALIDITY)){
								if(ltyRewardExpLogList == null){
									ltyRewardExpLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								ltyRewardExpLogList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_GIFT_AMOUNT_VALIDITY)){
								if(giftAmtExpLogList == null){
									giftAmtExpLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								giftAmtExpLogList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_LOYALTY_MEMBERSHIP_VALIDITY)){
								if(ltyMemExpLogList == null){
									ltyMemExpLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								ltyMemExpLogList.add(ltySettingsActivityLogs);
							}
							else if(ltySettingsActivityLogs.getLogType().equalsIgnoreCase(OCConstants.LTY_ACTIVITY_LOG_TYPE_GIFT_CARD_VALIDITY)){
								if(giftCardExpLogList == null){
									giftCardExpLogList = new ArrayList<LtySettingsActivityLogs>();
								}
								giftCardExpLogList.add(ltySettingsActivityLogs);
							}
						}
						else{
							//stop process
							continue;
						}
					}
					//using the lists send alerts
					StringBuffer sb = null;
					String htmlContent = PropertyUtil.getPropertyValueFromDB("emailAlertForLtyPrgmEditTemplate");

					//Tier Settings
					if(tierLogsList != null && tierLogsList.size() > 0){
						logger.debug("======tierLogsList====="+tierLogsList.size());
						sb = prepareTierSettingsHtml(tierLogsList);
					}
					htmlContent = htmlContent.replace("[tiersList]", (sb == null ? "" : sb.toString()));

					//Loyalty Rewards Expiration Settings
					sb = null;
					if(ltyRewardExpLogList != null && ltyRewardExpLogList.size() > 0){
						logger.debug("======ltyRewardExpLogList====="+ltyRewardExpLogList.size());
						sb = prepareLtyRewardExpHtml(ltyRewardExpLogList);
					}
					htmlContent = htmlContent.replace("[rewardValidity]", (sb == null ? "" : sb.toString()));

					//Gift Amount Expiration Settings
					sb = null;
					if(giftAmtExpLogList != null && giftAmtExpLogList.size() > 0){
						logger.debug("======giftAmtExpLogList====="+giftAmtExpLogList.size());
						sb = prepareGiftAmtExpHtml(giftAmtExpLogList);
					}
					htmlContent = htmlContent.replace("[giftAmtValidity]", (sb == null ? "" : sb.toString()));

					//Loyalty Membership Expiration Settings
					sb = null;
					if(ltyMemExpLogList != null && ltyMemExpLogList.size() > 0){
						logger.debug("======ltyMemExpLogList1====="+ltyMemExpLogList.size());
						sb = prepareLtyMemExpHtml(ltyMemExpLogList);
					}
					htmlContent = htmlContent.replace("[ltyMemValidity]", (sb == null ? "" : sb.toString()));

					//Reset Loyalty Membership Validity on Level Upgrade
					sb = null;
					if(ltyMemExpLogList != null && ltyMemExpLogList.size() > 0){
						logger.debug("======ltyMemExpLogList2====="+ltyMemExpLogList.size());
						sb = prepareResetValidityHtml(ltyMemExpLogList);
					}
					htmlContent = htmlContent.replace("[ltyResetValidityOnUpgd]", (sb == null ? "" : sb.toString()));

					//Gift Membership Expiration Settings
					sb = null;
					if(giftCardExpLogList != null && giftCardExpLogList.size() > 0){
						logger.debug("======giftCardExpLogList====="+giftCardExpLogList.size());
						sb = prepareGiftMemExpHtml(giftCardExpLogList);
					}
					htmlContent = htmlContent.replace("[giftMemValidity]", (sb == null ? "" : sb.toString()));

					//Bonus Settings
					sb = null;
					if(bonusLogList != null && bonusLogList.size() > 0){
						logger.debug("======bonusLogList====="+bonusLogList.size());
						sb = prepareBonusSettingsHtml(bonusLogList);
					}
					htmlContent = htmlContent.replace("[bonusList]", (sb == null ? "" : sb.toString()));

					//OTP Settings
					sb = null;					
					if(otpLogList != null && otpLogList.size() > 0){
						logger.debug("======otpLogList====="+otpLogList.size());
						sb = prepareOTPSettingsHtml(otpLogList);
					}
					htmlContent = htmlContent.replace("[otpSetting]", (sb == null ? "" : sb.toString()));

					//logger.debug("htmlContent is::::\n"+htmlContent);
					//send mail and sms...
					LtySettingsActivityLogsDao ltySettingsActivityLogsDao = (LtySettingsActivityLogsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
					LtySettingsActivityLogsDaoForDML ltySettingsActivityLogsDaoForDML = (LtySettingsActivityLogsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
					if(sendAlert){
						Calendar modifiedDate = ltySettingsActivityLogsDao.getLastModifiedDateByPrgmId(prgmObj.getProgramId());
						if(modifiedDate != null){
							sendAlerts(prgmObj, htmlContent, modifiedDate);
						}

					}

					//Update Logs Object with sendEmailFlag to 'N'
					List<LtySettingsActivityLogs> updateLogs = new ArrayList<LtySettingsActivityLogs>();
					if(tierLogsList != null && tierLogsList.size() > 0){
						for (LtySettingsActivityLogs log : tierLogsList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(ltyRewardExpLogList != null && ltyRewardExpLogList.size() > 0){
						for (LtySettingsActivityLogs log : ltyRewardExpLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(giftAmtExpLogList != null && giftAmtExpLogList.size() > 0){
						for (LtySettingsActivityLogs log : giftAmtExpLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(ltyMemExpLogList != null && ltyMemExpLogList.size() > 0){
						for (LtySettingsActivityLogs log : ltyMemExpLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(giftCardExpLogList != null && giftCardExpLogList.size() > 0){
						for (LtySettingsActivityLogs log : giftCardExpLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(bonusLogList != null && bonusLogList.size() > 0){
						for (LtySettingsActivityLogs log : bonusLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					if(otpLogList != null && otpLogList.size() > 0){
						for (LtySettingsActivityLogs log : otpLogList) {
							log.setSendEmailFlag(OCConstants.FLAG_NO);
							log.setLastEmailSentDate(Calendar.getInstance());
							updateLogs.add(log);
						}
					}
					ltySettingsActivityLogsDaoForDML.saveByCollection(updateLogs);
				}
			}

			


		} catch (Exception e) {
			logger.error("Exception in :: ", e);
		}
		logger.info(">>> Completed LoyaltyProgramEditALerts timer >>>");
	}

	private void sendAlerts(LoyaltyProgram prgmObj, String htmlContent, Calendar modifiedDate) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(prgmObj.getUserId());
			
			int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE));
			int timezoneDiffrenceMinutesInt = 0;
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt - serverTimeZoneValInt;
			
			modifiedDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
			
			String modifiedDateStr = MyCalendar.calendarToString(modifiedDate, MyCalendar.FORMAT_DATETIME_WITH_DELIMETER);
			
			if(user.getEmailId() != null && !user.getEmailId().isEmpty()){
				htmlContent = htmlContent.replace("[fname]", user.getFirstName());
				htmlContent = htmlContent.replace("[programName]", prgmObj.getProgramName());
				htmlContent = htmlContent.replace("[username]", Utility.getOnlyUserName(user.getUserName()));
				htmlContent = htmlContent.replace("[modifiedDate]", modifiedDateStr);
				htmlContent = htmlContent.replace("[orgId]", Utility.getOnlyOrgId(user.getUserName()));
				//EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue email = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, htmlContent, Constants.EQ_STATUS_ACTIVE, user.getEmailId(), user, MyCalendar.getNewCalendar().getTime(), "OptCulture Alert: Change in Loyalty Program", null , null);
				//emailQueueDao.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(email);
				EmailQueue emailForSupport = new EmailQueue("OptCulture Alert: Change in Loyalty Program",htmlContent,Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE,"alerts@optculture.com", MyCalendar.getNewCalendar().getTime());
				emailQueueDaoForDML.saveOrUpdate(emailForSupport);

				if(!user.isEnableSMS()) return;

				if(user.getPhone() != null  && !user.getPhone().isEmpty()){
					String message = PropertyUtil.getPropertyValueFromDB("smsAlertForLtyPrgmEditTemplate");
					message = message.replace("[programName]", prgmObj.getProgramName());
					message = message.replace("[modifiedDate]", modifiedDateStr);
					message = message.replace("[emailId]", user.getEmailId());

					//validate user phone no
					Map<String, Object> resultMap = null;

					resultMap = LoyaltyProgramHelper.validateMobile(user,user.getPhone());
					String phone = (String) resultMap.get("phone"); 
					boolean phoneIsValid = (Boolean) resultMap.get("isValid");

					if(phoneIsValid){
						LoyaltyProgramHelper.sendSmsAlert(user, phone, message);
					}
				}
			}
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}

	private StringBuffer prepareOTPSettingsHtml(List<LtySettingsActivityLogs> otpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(otpLogList.size() == 1){
			arr = otpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(otpLogList.size() > 1){
			arr = otpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = otpLogList.get(otpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}

		String[] beforeArr = beforeStr.split(Constants.DELIMETER_COMMA);
		String[] afterArr = afterStr.split(Constants.DELIMETER_COMMA);
		int length = beforeArr.length;
		String enabled = "";
		String amt = "";

		StringBuffer sb = new StringBuffer();
		if(!beforeArr[length - 2].equalsIgnoreCase(afterArr[length - 2]) || !beforeArr[length - 1].equalsIgnoreCase(afterArr[length - 1])){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">OTP Authentication for Redemption</b><hr style=\"color: #ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enabled</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redeem Amount Greater Than</th></tr>");

			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			enabled = beforeArr[length - 2].equalsIgnoreCase("Y") ? "Yes" : "No";
			amt = beforeArr[length - 1].equalsIgnoreCase("--") ? "--" : "$"+beforeArr[length - 1];
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+enabled+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+amt+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");

			enabled = afterArr[length - 2].equalsIgnoreCase("Y") ? "Yes" : "No";
			amt = afterArr[length - 1].equalsIgnoreCase("--") ? "--" : "$"+afterArr[length - 1];
			if(!beforeArr[length - 2].equalsIgnoreCase(afterArr[length - 2])){
				sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+enabled+"</td>");
			}
			else{
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+enabled+"</td>");
			}
			if(!beforeArr[length - 1].equalsIgnoreCase(afterArr[length - 1])){
				sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+amt+"</td></tr></table>");
			}
			else{
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+amt+"</td></tr></table>");
			}
		}
		return sb;
	}

	private StringBuffer prepareBonusSettingsHtml(List<LtySettingsActivityLogs> bonusLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(bonusLogList.size() == 1){
			arr = bonusLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(bonusLogList.size() > 1){
			arr = bonusLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = bonusLogList.get(bonusLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}
		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");
		int length = beforeArr.length > afterArr.length ? beforeArr.length : afterArr.length;
		StringBuffer sb = new StringBuffer();
		if(afterArr[0].equalsIgnoreCase("null") && !beforeArr[0].equalsIgnoreCase("null")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Bonus</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\">");
			sb.append("<tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Previous Setting</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Current Setting</th></tr>");
			sb.append("<tr style=\"background:#444444;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th></tr>");
			for (int i = 0; i < length; i++) {
				String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[1]+"</td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
			}
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("null") && !afterArr[0].equalsIgnoreCase("null")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Bonus</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\">");
			sb.append("<tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Previous Setting</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Current Setting</th></tr>");
			sb.append("<tr style=\"background : #444444;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th></tr>");
			for (int i = 0; i < length; i++) {
				String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[1]+"</td></tr>");
			}
			sb.append("</table>");
		}
		else if(!beforeArr[0].equalsIgnoreCase("null") && !afterArr[0].equalsIgnoreCase("null")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Bonus</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\">");
			sb.append("<tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Previous Setting</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\" colspan=\"2\">Current Setting</th></tr>");
			sb.append("<tr style=\"background : #444444;color:#ffffff;\"><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Threshold Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Bonus</th></tr>");
			for (int i = 0; i < length; i++) {
				if(beforeArr.length == afterArr.length || (beforeArr.length > afterArr.length && i < afterArr.length) ||
						(beforeArr.length < afterArr.length && i < beforeArr.length)){
					String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
					String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
					sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[1]+"</td>");
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[1]+"</td></tr>");
				}
				else if(beforeArr.length > afterArr.length && i >= afterArr.length){
					String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
					sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[1]+"</td>");
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
				}
				else if(beforeArr.length < afterArr.length && i >= beforeArr.length){
					String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
					sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[1]+"</td></tr>");
				}
			}
			sb.append("</table>");
		}
		return sb;
	}

	private StringBuffer prepareGiftMemExpHtml(List<LtySettingsActivityLogs> giftCardExpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(giftCardExpLogList.size() == 1){
			arr = giftCardExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(giftCardExpLogList.size() > 1){
			arr = giftCardExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = giftCardExpLogList.get(giftCardExpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}
		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		String beforeExp = "";
		String afterExp = "";
		StringBuffer sb = new StringBuffer();
		if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("N")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Card Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
			String[] before = beforeArr[1].split(Constants.DELIMETER_COMMA);
			beforeExp =  before[1] + " " + before[0] + OCConstants.MORETHANONEOCCURENCE;
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ beforeExp +"</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("N") && afterArr[0].equalsIgnoreCase("Y")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Card Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
			String[] after = afterArr[1].split(Constants.DELIMETER_COMMA);
			afterExp =  after[1] + " " + after[0] + OCConstants.MORETHANONEOCCURENCE;
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ afterExp +"</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("Y")){
			if(!beforeArr[1].equalsIgnoreCase(afterArr[1])){
				sendAlert = true;
				sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Card Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
				sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
				String[] before = beforeArr[1].split(Constants.DELIMETER_COMMA);
				beforeExp =  before[1] + " " + before[0] + OCConstants.MORETHANONEOCCURENCE;
				String[] after = afterArr[1].split(Constants.DELIMETER_COMMA);
				afterExp =  after[1] + " " + after[0] + OCConstants.MORETHANONEOCCURENCE;
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ beforeExp +"</td></tr>");
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
				sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ afterExp +"</td></tr>");
				sb.append("</table>");
			}
		}
		return sb;
	}

	private StringBuffer prepareResetValidityHtml(List<LtySettingsActivityLogs> ltyMemExpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(ltyMemExpLogList.size() == 1){
			arr = ltyMemExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(ltyMemExpLogList.size() > 1){
			arr = ltyMemExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = ltyMemExpLogList.get(ltyMemExpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}

		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		StringBuffer sb = new StringBuffer();
		if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("N") && beforeArr[1].equalsIgnoreCase("Y")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Reset Loyalty Membership Validity on Level Upgrade</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enabled</th></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Yes</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">No</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("N") && afterArr[0].equalsIgnoreCase("Y")  && afterArr[1].equalsIgnoreCase("Y")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Reset Loyalty Membership Validity on Level Upgrade</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enabled</th></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">No</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Yes</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("Y") && !beforeArr[1].equalsIgnoreCase(afterArr[1])){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Reset Loyalty Membership Validity on Level Upgrade</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Enabled</th></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			String enabled = beforeArr[1].equalsIgnoreCase("Y") ? "Yes" : "No";
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+enabled+"</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			enabled = afterArr[1].equalsIgnoreCase("Y") ? "Yes" : "No";
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+enabled+"</td></tr>");
			sb.append("</table>");
		}
		return sb;
	}

	private StringBuffer prepareLtyMemExpHtml(List<LtySettingsActivityLogs> ltyMemExpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(ltyMemExpLogList.size() == 1){
			arr = ltyMemExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(ltyMemExpLogList.size() > 1){
			arr = ltyMemExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = ltyMemExpLogList.get(ltyMemExpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}

		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		String beforeExp = "";
		String afterExp = "";
		StringBuffer sb = new StringBuffer();
		if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("N")){
			for (int i = 2; i < beforeArr.length; i++) {
				String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
				if(!before[1].equalsIgnoreCase("null")){
					if(sb.length() == 0) {
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
					beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		else if(beforeArr[0].equalsIgnoreCase("N") && afterArr[0].equalsIgnoreCase("Y")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
			for (int i = 2; i < afterArr.length; i++) {
				String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
				if(!after[1].equalsIgnoreCase("null")){
					if(sb.length() == 0) {
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
					afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
					sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		else if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("Y")){
			int length = beforeArr.length > afterArr.length ? beforeArr.length : afterArr.length;
			for (int i = 2; i < length; i++) {
				if(beforeArr.length == afterArr.length || (beforeArr.length > afterArr.length && i < afterArr.length) ||
						(beforeArr.length < afterArr.length && i < beforeArr.length)){
					if(!beforeArr[i].equalsIgnoreCase(afterArr[i])){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
						String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						if(before[1].equalsIgnoreCase("null")) beforeExp = "--";
						else beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
						if(after[1].equalsIgnoreCase("null")) afterExp = "--";
						afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
					}
				}
				else if(beforeArr.length > afterArr.length && i >= afterArr.length){
					String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
					if(!before[1].equalsIgnoreCase("null")){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
					}
				}
				else if(beforeArr.length < afterArr.length && i >= beforeArr.length){
					String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
					if(!after[1].equalsIgnoreCase("null")){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Membership Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
					}
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		return sb;
	}

	private StringBuffer prepareGiftAmtExpHtml(List<LtySettingsActivityLogs> giftAmtExpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(giftAmtExpLogList.size() == 1){
			arr = giftAmtExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(giftAmtExpLogList.size() > 1){
			arr = giftAmtExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = giftAmtExpLogList.get(giftAmtExpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}
		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		String beforeExp = "";
		String afterExp = "";
		StringBuffer sb = new StringBuffer();
		if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("N")){
			sendAlert = true;
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Amount Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
			String[] before = beforeArr[1].split(Constants.DELIMETER_COMMA);
			beforeExp =  before[1] + " " + before[0] + OCConstants.MORETHANONEOCCURENCE;
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ beforeExp +"</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("N") && afterArr[0].equalsIgnoreCase("Y")){
			sendAlert = true;			
			sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Amount Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
			sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
			String[] after = afterArr[1].split(Constants.DELIMETER_COMMA);
			afterExp =  after[1] + " " + after[0] + OCConstants.MORETHANONEOCCURENCE;
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
			sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
			sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
			sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ afterExp +"</td></tr>");
			sb.append("</table>");
		}
		else if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("Y")){
			if(!beforeArr[1].equalsIgnoreCase(afterArr[1])){
				sendAlert = true;
				sb.append("<br/><br/><b style=\"margin-left:20px;\">Gift Amount Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
				sb.append("<table style=\"width:30%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
				String[] before = beforeArr[1].split(Constants.DELIMETER_COMMA);
				beforeExp =  before[1] + " " + before[0] + OCConstants.MORETHANONEOCCURENCE;
				String[] after = afterArr[1].split(Constants.DELIMETER_COMMA);
				afterExp =  after[1] + " " + after[0] + OCConstants.MORETHANONEOCCURENCE;
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
				sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ beforeExp +"</td></tr>");
				sb.append("<tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
				sb.append("<td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+ afterExp +"</td></tr>");
				sb.append("</table>");
			}
		}
		return sb;
	}

	private StringBuffer prepareLtyRewardExpHtml(List<LtySettingsActivityLogs> ltyRewardExpLogList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(ltyRewardExpLogList.size() == 1){
			arr = ltyRewardExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(ltyRewardExpLogList.size() > 1){
			arr = ltyRewardExpLogList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = ltyRewardExpLogList.get(ltyRewardExpLogList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}
		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		String beforeExp = "";
		String afterExp = "";
		StringBuffer sb = new StringBuffer();
		if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("N")){
			for (int i = 1; i < beforeArr.length; i++) {
				String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
				if(!before[1].equalsIgnoreCase("null")){
					if(sb.length() == 0){
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Reward Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
					beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
					sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		else if(beforeArr[0].equalsIgnoreCase("N") && afterArr[0].equalsIgnoreCase("Y")){
			for (int i = 1; i < afterArr.length; i++) {
				String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
				if(!after[1].equalsIgnoreCase("null")){
					if(sb.length() == 0){
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Reward Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
					afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
					sb.append("<td  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		else if(beforeArr[0].equalsIgnoreCase("Y") && afterArr[0].equalsIgnoreCase("Y")){
			int length = beforeArr.length > afterArr.length ? beforeArr.length : afterArr.length;
			for (int i = 1; i < length; i++) {
				if(beforeArr.length == afterArr.length || (beforeArr.length > afterArr.length && i < afterArr.length) ||
						(beforeArr.length < afterArr.length && i < beforeArr.length)){
					if(!beforeArr[i].equalsIgnoreCase(afterArr[i])){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Reward Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
						String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						if(before[1].equalsIgnoreCase("null")) beforeExp = "--";
						else beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
						if(after[1].equalsIgnoreCase("null")) afterExp = "--";
						afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
					}
				}
				else if(beforeArr.length > afterArr.length && i >= afterArr.length){
					String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
					if(!before[1].equalsIgnoreCase("null")){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Reward Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						beforeExp = before[2] + " " + before[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+beforeExp+"</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr>");
					}
				}
				else if(beforeArr.length < afterArr.length && i >= beforeArr.length){
					String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
					if(!after[1].equalsIgnoreCase("null")){
						if(sb.length() == 0) {
							sendAlert = true;
							sb.append("<br/><br/><b style=\"margin-left:20px;\">Loyalty Reward Expiration Settings</b><hr style=\"color:#ffffff;\"><br/>");
							sb.append("<table style=\"width:40%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Expiration Period</th></tr>");
						}
						sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
						afterExp = after[2] + " " + after[1] + OCConstants.MORETHANONEOCCURENCE;
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td></tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td><td  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+afterExp+"</td></tr>");
					}
				}
			}
			if(sb.length() > 0){
				sb.append("</table>");
			}
		}
		return sb;
	}

	private StringBuffer prepareTierSettingsHtml(List<LtySettingsActivityLogs> tierLogsList) {
		String[] arr = null;
		String beforeStr = null;
		String afterStr = null;
		if(tierLogsList.size() == 1){
			arr = tierLogsList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			afterStr = arr[2];
		}
		else if(tierLogsList.size() > 1){
			arr = tierLogsList.get(0).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			beforeStr = arr[1];
			arr = tierLogsList.get(tierLogsList.size() - 1).getLogDetails().split(Constants.DELIMETER_DOUBLECOLON);
			afterStr = arr[2];
		}
		String[] beforeArr = beforeStr.split("\\|\\|");
		String[] afterArr = afterStr.split("\\|\\|");

		StringBuffer sb = new StringBuffer();

		int length = beforeArr.length > afterArr.length ? beforeArr.length : afterArr.length;
		for (int i = 0; i < length; i++) {
			if(beforeArr.length == afterArr.length || (beforeArr.length > afterArr.length && i < afterArr.length) ||
					(beforeArr.length < afterArr.length && i < beforeArr.length)){
				if(!beforeArr[i].equalsIgnoreCase(afterArr[i])){
					if(sb.length() == 0) {
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Tier Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Name</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Earn Rule</th>"
								+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redeem Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Activation Period</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Pts. Conversion Decision</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Upgrade Rule</th></tr>");
					}
					String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
					String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
					if(before[1].equalsIgnoreCase("null")){
						sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					}
					else {
						for (int j = 1; j < before.length; j++){
							sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[j]+"</td>");
						}
					}
					sb.append("</tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
					if(after[1].equalsIgnoreCase("null")) {
						sb.append("<td colspan=\"6\"  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">DELETED</td></tr>");
					}
					else {
						for (int j = 1; j < after.length; j++) {
							if(!before[1].equalsIgnoreCase("null") && before[j].equalsIgnoreCase(after[j])){
								sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[j]+"</td>");
							}
							else{
								sb.append("<td  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[j]+"</td>");
							}
						}
						sb.append("</tr>");
					}
				}
			}
			else if(beforeArr.length > afterArr.length && i >= afterArr.length){
				String[] before = beforeArr[i].split(Constants.DELIMETER_COMMA);
				if(!before[1].equalsIgnoreCase("null")){
					if(sb.length() == 0) {
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Tier Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Name</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Earn Rule</th>"
								+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redeem Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Activation Period</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Pts. Conversion Decision</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Upgrade Rule</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
					for (int j = 1; j < before.length; j++){
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+before[j]+"</td>");
					}
					sb.append("</tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
					sb.append("<td colspan=\"6\"  style=\"background:#FFF7D3;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">DELETED</td></tr>");
				}
			}
			else if(beforeArr.length < afterArr.length && i >= beforeArr.length){
				String[] after = afterArr[i].split(Constants.DELIMETER_COMMA);
				if(!after[1].equalsIgnoreCase("null")){
					if(sb.length() == 0) {
						sendAlert = true;
						sb.append("<br/><br/><b style=\"margin-left:20px;\">Tier Settings</b><hr style=\"color:#ffffff;\"><br/>");
						sb.append("<table style=\"width:90%;margin-left:20px;border-collapse:collapse;\"><tr style=\"background:#CA0000;color:#ffffff;\"><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:none;background:#ffffff;padding: 5px;font-size: 13;text-align: center;\"></th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Name</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Earn Rule</th>"
								+ "<th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Redeem Rule</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Activation Period</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Pts. Conversion Decision</th><th style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Tier Upgrade Rule</th></tr>");
					}
					sb.append("<tr><td rowspan=\"2\" style=\"background:#444444;color:#ffffff;border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[0]+"</td><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Previous Setting</td>");
					sb.append("<td colspan=\"6\" style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">--</td>");
					sb.append("</tr><tr><td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">Current Setting</td>");
					for (int j = 1; j < after.length; j++){
						sb.append("<td style=\"border:1px solid #D1D6DA;padding:5px;font-size:13;text-align:center;\">"+after[j]+"</td>");
					}
					sb.append("</tr>");
				}
			}
		}
		if(sb.length() > 0){
			sb.append("</table>");
		}
		return sb;
	}

	private List<LtySettingsActivityLogs> fetchLogs() throws Exception{

		List<LtySettingsActivityLogs> listOfLogs = null;
		try{
			LtySettingsActivityLogsDao ltySettingsActivityLogsDao = (LtySettingsActivityLogsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
			listOfLogs = ltySettingsActivityLogsDao.fetchAllLogsToBeSent();
			if(listOfLogs != null && listOfLogs.size() > 0){
				return listOfLogs;
			}
		}catch(Exception e){
			logger.error("Exception in dao service...");
		}
		return listOfLogs;
	}
}
