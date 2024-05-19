package org.mq.optculture.timer;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ExportFileDetails;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.ExportFileDetailsDao;
import org.mq.marketer.campaign.dao.ExportFileDetailsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ExportFileCleanUpTimer extends TimerTask implements ApplicationContextAware{
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	public void run(){
		try{
			logger.info("ExportFileCleanUpTimer ********Calling here ********* ");
			
			ExportFileCleanUp();
			logger.info("ExportFileCleanUpTimer ********end  here ********* ");
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}		
	}//run
	
	
	private void ExportFileCleanUp() {
		
		try {
			ExportFileDetailsDao  fileExportDetailsDao = (ExportFileDetailsDao)context.getBean("exportFileDetailsDao");
			ExportFileDetailsDaoForDML  fileExportDetailsDaoForDML = (ExportFileDetailsDaoForDML)context.getBean("exportFileDetailsDaoForDML");
			List<ExportFileDetails> list = fileExportDetailsDao.findAllByStatus();
			if(list == null || list.size() == 0){
				logger.info("No export file existed for clean up");
				return;
			}
			File deletedFile = null;
			for (ExportFileDetails eachExportfile : list) {
				
				// Get the represented date in milliseconds
		        long milis1 = eachExportfile.getCreatedTime().getTimeInMillis();
		        long milis2 = Calendar.getInstance().getTimeInMillis();
				
		        // Calculate difference in milliseconds
		        long diff = milis2 - milis1;
		        
		        // Calculate difference in days
		        long diffDays = diff / (24 * 60 * 60 * 1000);
//		        long diffHours = diff / (60 * 60 * 1000) % 24;
		        /*long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);*/
		        
				if(diffDays <= 7l) continue;
//		       if(!(diffHours >= 1)) continue;
					
				deletedFile = new File(eachExportfile.getFilePath());
				deletedFile.delete();
					
				eachExportfile.setDeletedTime(Calendar.getInstance());
				eachExportfile.setStatus(Constants.EXPORT_FILE_DELETED);
				//fileExportDetailsDao.saveOrUpdate(eachExportfile);
				fileExportDetailsDaoForDML.saveOrUpdate(eachExportfile);
				
				logger.info("File update in DB and the file name is  ::"+eachExportfile.getFilePath());
			}
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
