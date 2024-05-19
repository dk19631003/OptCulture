package org.mq.captiway.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;
import org.mq.captiway.scheduler.utility.Constants;

public class FailedSalesFileAlertTimerTask {

	private static String failFolder = PropertyUtil.getPropertyValue("failFolder");
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private EmailQueueDaoForDML emailQueueDaoForDML = null;

	
	@Scheduled(cron="0 1 1 * * ?")
	public void failedSalesFileAlertTimer() {

		try {
			//failFolder = "E:\\VenkataRatna_Motupalli\\failFolder\\";
			String failedFolder = failFolder;
			
			File failedFiles = new File(failedFolder);
			File[] sourceFileLst =  failedFiles.listFiles();

			String ListOfFile = "";
			int noOfFiles = 0;
			
			for (File eachfailedFile : sourceFileLst) {

				Calendar cal = Calendar.getInstance();

				BasicFileAttributes attrs = Files.readAttributes(eachfailedFile.toPath(), BasicFileAttributes.class);
				FileTime time = attrs.creationTime();
				
				//String pattern = "yyyy-MM-dd HH:mm:ss";
				String pattern = MyCalendar.FORMAT_DATETIME_STYEAR;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				String formatted = simpleDateFormat.format( new Date( time.toMillis() ) );
				logger.info( "The file creation date and time is11111: " + formatted );

				Calendar c1 = simpleDateFormat.getCalendar();
				c1.set(MyCalendar.DATE,c1.get(MyCalendar.DATE)+1);

				String formatted111 = simpleDateFormat.format(c1.getTime());
				logger.info( "The file creation date and time is2222: " + formatted111 );

				if(cal.compareTo(c1) < 0) {
					logger.info(formatted111 + " > "+  formatted);
					ListOfFile += eachfailedFile.getName()+"<br/>";
					noOfFiles +=1;
				}

				logger.info(cal.compareTo(c1));
				logger.info(c1.compareTo(cal));
			}    
			
			//if(!ListOfFile.trim().isEmpty()) {
			if(noOfFiles>0) {	
				try {
					logger.info("List of files : "+ListOfFile);
					sendMailToSupport(ListOfFile,noOfFiles);
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error("***** Exception : Failed csv sending a Mail to Support", e);
					//e.printStackTrace();
				}
				
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("***** Exception : Support mail", e);
		}
	}

	
	private void sendMailToSupport(String filesList,int numberOfFiles) throws BaseServiceException{

		try {
			String message = PropertyUtil.getPropertyValueFromDB("failedFilesMessageTemplate");
			String subject = PropertyUtil.getPropertyValueFromDB("failedFilesSubject");
			String supportMailId = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
			Calendar cal = Calendar.getInstance();
			emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			//message = message.replace(Constants.LIST_FILES, filesList);
			message = message.replace(Constants.NO_OF_FILES, numberOfFiles+"");
			
			String date = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_MONTH_YEAR);
			logger.info(" date :"+date);
			String datearr[] = (date!=null && !date.isEmpty())?date.split(" "):null;
			if(datearr!=null && datearr.length>0) {
				String finaldate = datearr[0]+getDayOfMonthSuffix(Integer.parseInt(datearr[0]));
				date = date.replace(datearr[0], finaldate);
				logger.info("date after replace  :"+date);
				subject = subject.replace(Constants.DATE, date);
			}
			EmailQueue eqObj =new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,Constants.EQ_STATUS_ACTIVE, supportMailId, new Date());
			emailQueueDaoForDML.saveOrUpdate(eqObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("***** Exception :sendMailToSupport Failed csv sending a Mail to Support", e);
		}
	}
	
		
	public static String getDayOfMonthSuffix(final int n) {
	    if (n >= 11 && n <= 13) {
	        return "th";
	    }
	    switch (n % 10) {
	        case 1:  return "st";
	        case 2:  return "nd";
	        case 3:  return "rd";
	        default: return "th";
	    }
	}
}
