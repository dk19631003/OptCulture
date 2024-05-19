package org.mq.optculture.timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.ExternalSMTPSupportSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Sessions;

public class OptSyncUpdateTimer extends TimerTask {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public final static long MINUTE_MILLIS = 1000*60;
	
	
	@Override
	public void run() {
		processOptSyncData();
	}

	
	public void processOptSyncData(){
		try {
			String serverName = PropertyUtil.getPropertyValue("subscriberIp");
			ExternalSMTPSupportSender externalSMTPSupportSender=new ExternalSMTPSupportSender();
			UpdateOptSyncDataDao updateOptSyncDataDao =(UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName("updateOptSyncDataDao");
			UpdateOptSyncDataDaoForDML updateOptSyncDataDaoForDML =(UpdateOptSyncDataDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("updateOptSyncDataDaoForDML");
			UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			List<UpdateOptSyncData> updateOptSyncList = updateOptSyncDataDao.findAllBy();
			Date now = Calendar.getInstance().getTime();
			String fromEmailId = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
			//String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
			
			List<String> emailList = new ArrayList<String>();
			Long userId=null;
			String messageHeader="";
			String htmlContent="";
			String textContent="";
			int count =0;
			String toEmailId1=Constants.STRING_NILL;
			String[] toEmailId = null;
			boolean isNew = false;
			 //TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			if(updateOptSyncList != null && updateOptSyncList.size() > 0 ) {
				int minDiff = 0;
				int downDiff = 0;
				for (UpdateOptSyncData updateOptSyncData : updateOptSyncList) {
					if(updateOptSyncData.getOptSyncHitTime() != null){
						minDiff =(int) ((now.getTime() / MINUTE_MILLIS) -(updateOptSyncData.getOptSyncHitTime().getTime().getTime() / MINUTE_MILLIS) );
					}
					logger.info("min dif is:::"+minDiff);
					if(OCConstants.OPT_SYNC_STATUS_ACTIVE.equals(updateOptSyncData.getStatus())){
						
						if(minDiff > OCConstants.OPT_SYNC_TIMER_PERIOD){
						
							
							 updateOptSyncData.setStatus(OCConstants.OPT_SYNC_STATUS_DEACTIVE);
							 isNew = true;
							 count = count+ 1;
						}
					}
					logger.info("is new is:::"+isNew+ "::::count is::::"+count);
					if(!isNew && OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus()) ){
						
						if(minDiff > OCConstants.OPT_SYNC_TIMER_PERIOD) count  =  updateOptSyncData.getCount()+ 1;
					}
					
					updateOptSyncData.setCount(count);
					userId= updateOptSyncData.getUserId();
					Users user = usersDao.findByUserId(userId);
					
					// Down time string
					
				/*	String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
					int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
					String timezoneDiffrenceMinutes = user.getClientTimeZone();
					int timezoneDiffrenceMinutesInt = 0;
					
					if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
					timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
					
					Calendar cal = updateOptSyncData.getOptSyncHitTime();
					if(cal != null)cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
					TimeZone tz = cal.getTimeZone();
					logger.info("time zone value is::"+tz);*/
					
					String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
					int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
					String timezoneDiffrenceMinutes = user.getClientTimeZone();
					int timezoneDiffrenceMinutesInt = 0;
					
					if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
					timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
					
					Calendar cal1 = updateOptSyncData.getOptSyncHitTime();
					Calendar cal =  Calendar.getInstance();
					cal.setTimeInMillis(cal1.getTimeInMillis());
					cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
					
					
					//String downDateStr  = MyCalendar.calendarToString(updateOptSyncData.getOptSyncHitTime(), MyCalendar.FORMAT_DATETIME_STYEAR );
					String downDateStr  = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR );
					DateFormat df = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					  DateFormat outputformat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");
				       Date date = null;
				       String downTimeStr = Constants.STRING_NILL;
				       try{
				    	  date= df.parse(downDateStr);
				    	  downTimeStr = outputformat.format(date);
				    	}catch(ParseException e){
				    	    logger.error(" Exception in date format convertion", e);
				    	 }
				       logger.info("count after status change is::::::"+count+" :: down time str is:::"+downTimeStr);
					if(count >= 3){
						//emailList.add(updateOptSyncData.getEmailId());
						toEmailId1 = updateOptSyncData.getEmailId();
						toEmailId = new String[5];// Assuming only five emailids are allowed
						toEmailId = toEmailId1.split(","); 
						
						
					}
						
					updateOptSyncData.setOptSyncModifiedTime(Calendar.getInstance());
					
					
					
					
					String optSyncName=updateOptSyncData.getOptSyncName().trim();
					String optSyncId = ""+updateOptSyncData.getOptSyncId();
					String userName = Utility.getOnlyUserName(user.getUserName());
					String orgId=user.getUserOrganization().getOrgExternalId();
					String senderName=Constants.OPTSYNC_SENDER_NAME_VALUE;
					
				
				htmlContent =  PropertyUtil.getPropertyValueFromDB(Constants.OPTSYNC_DOWN_TIME_PROPERTY);
					
				htmlContent =	htmlContent.replace(Constants.OPTSYNC_DOWN_TIME_STR, downTimeStr)
										   .replace(Constants.OPTSYNC_NAME, optSyncName)
							   			   .replace(Constants.OPTSYNC_ID, optSyncId)
							   			   .replace(Constants.OPTSYNC_USER_NAME, userName)
							   			   .replace(Constants.OPTSYNC_ORGID, orgId)
							   			   .replace(Constants.OPTSYNC_SENDER_NAME, senderName);
									   
				
			//	logger.info("final html content is:::"+htmlContent);
					
					// htmlContent="This is to inform that OptSync is down. Please contact Administrator";
					//"OptCulture Alert: OptSync Down (Username: "+userName+")" OPTSYNC_ALERT_MAIL_SUBJECT_PH
					String subject= OCConstants.OPTSYNC_ALERT_MAIL_SUBJECT.replace(OCConstants.OPTSYNC_ALERT_MAIL_SUBJECT_PH, userName); 
					 textContent = htmlContent;
					
						if(updateOptSyncData.getDownAlertSentTime() != null){
							downDiff =(int) ((now.getTime() / MINUTE_MILLIS) -(updateOptSyncData.getDownAlertSentTime().getTime().getTime() / MINUTE_MILLIS) );
						}
					
					//sending email
					//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
						if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
						try {
							if(user != null) {
								messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+OCConstants.OPTINL_MAIL_SENDER +"\",\"ServerName\": \""+ serverName +"\" }}";
							}
							//mail will be sent only if plugin is active 
							if(count >= 3 && OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus()) &&
									OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus()) && updateOptSyncData.getDownAlertSentTime() == null){
								
								externalSMTPSupportSender.submitOptSyncMail(messageHeader, htmlContent, textContent,fromEmailId,
										subject,toEmailId);
								updateOptSyncData.setDownAlertSentTime(Calendar.getInstance());
								
								
							}else if(count >= 3 && OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE.equalsIgnoreCase(updateOptSyncData.getPluginStatus()) &&
									OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus()) && 
									updateOptSyncData.getDownAlertSentTime() != null  && downDiff >= OCConstants.DOWN_ALERT_TIME_SPAN_DAY){
								externalSMTPSupportSender.submitOptSyncMail(messageHeader, htmlContent, textContent,fromEmailId,
										subject,toEmailId);
								updateOptSyncData.setDownAlertSentTime(Calendar.getInstance());
								
								
							}
							//updateOptSyncDataDao.saveOrUpdate(updateOptSyncData);
							updateOptSyncDataDaoForDML.saveOrUpdate(updateOptSyncData);
						} catch (Exception e) {
							logger.debug("Exception while sending through sendGridAPI .. returning ",e);
						}
					
					}
				}//for
				
			}
			
			/*Users user = usersDao.findByUserId(userId);
			String[] toEmail=  new String[emailList.size()];
			
			
			 htmlContent="This is to inform that opt sync was down.please contact admin.";
			
			 textContent = htmlContent;
			
			//sending email
			if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
				
				try {
					if(user != null) {
						messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+OCConstants.GENERAL_MAIL_SENDER +"\",\"ServerName\": \""+ serverName +"\" }}";
					}
					if(count >= 3){
						
						externalSMTPSupportSender.submitOptSyncMail(messageHeader, htmlContent, textContent,fromEmailId,
								"Opt Sync Alert Message",toEmail);
						
					}
				} catch (Exception e) {
					logger.debug("Exception while sending through sendGridAPI .. returning ",e);
				}
			}//if
*/			
			
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		
	}
}
