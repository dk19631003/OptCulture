package org.mq.captiway.scheduler.campaign;

import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.GoogleAnalyticsVisitsDownloads;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.GoogleAnalyticsVisitsDownloadsDao;
import org.mq.captiway.scheduler.dao.GoogleAnalyticsVisitsDownloadsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class GAVisitDownloads extends TimerTask  implements ApplicationContextAware{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private GoogleAnalyticsVisitsDownloads googleAnalyticsVisitsDownloads=null;
	
	
	public GAVisitDownloads(){}
	
	private GoogleAnalyticsVisitsDownloadsDao googleAnalyticsVisitsDownloadsDao;
	private GoogleAnalyticsVisitsDownloadsDaoForDML googleAnalyticsVisitsDownloadsDaoForDML;
	public GoogleAnalyticsVisitsDownloadsDaoForDML getGoogleAnalyticsVisitsDownloadsDaoForDML() {
		return googleAnalyticsVisitsDownloadsDaoForDML;
	}
	public void setGoogleAnalyticsVisitsDownloadsDaoForDML(
			GoogleAnalyticsVisitsDownloadsDaoForDML googleAnalyticsVisitsDownloadsDaoForDML) {
		this.googleAnalyticsVisitsDownloadsDaoForDML = googleAnalyticsVisitsDownloadsDaoForDML;
	}
	public GoogleAnalyticsVisitsDownloadsDao getGoogleAnalyticsVisitsDownloadsDao() {
		return googleAnalyticsVisitsDownloadsDao;
	}
	public void setGoogleAnalyticsVisitsDownloadsDao
						(GoogleAnalyticsVisitsDownloadsDao googleAnalyticsVisitsDownloadsDao) {
		this.googleAnalyticsVisitsDownloadsDao = googleAnalyticsVisitsDownloadsDao;
	}
	
	
	private ContactScoreSetting contactScoreSetting;
    public ContactScoreSetting getContactScoreSetting() {
		return contactScoreSetting;
	}
	public void setContactScoreSetting(ContactScoreSetting contactScoreSetting) {
		this.contactScoreSetting = contactScoreSetting;
	}
	
	private ApplicationContext applicationContext;
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
	public void run() {
		try {
			if(logger.isDebugEnabled()) logger.debug("-- just entered in gAVisitDownload run method ---");
			String scoreGroupName=null;
			Users user=null;
			String emailName=null;
	
			List<GoogleAnalyticsVisitsDownloads> contactGAVDLst= googleAnalyticsVisitsDownloadsDao.findStatus(Constants.GA_ACTION_TYPE);
			if(logger.isDebugEnabled()) logger.debug("getting records from DB   :"+contactGAVDLst.size());
			
			for (Object obj : contactGAVDLst) {
				
				googleAnalyticsVisitsDownloads=(GoogleAnalyticsVisitsDownloads)obj;
				user=googleAnalyticsVisitsDownloads.getUser();
				emailName=googleAnalyticsVisitsDownloads.getEmailName().trim();
					
				if(googleAnalyticsVisitsDownloads.getAction().equalsIgnoreCase(Constants.GA_PAGE_VISIT) && 
						googleAnalyticsVisitsDownloads.getStatus().equalsIgnoreCase(Constants.GA_ACTION_TYPE)) {
					scoreGroupName=Constants.SCORE_PAGE_VISIT;
					if(logger.isDebugEnabled()) logger.debug("--------------ContactSCoreSettings update method is called by PageVisited----");
					contactScoreSetting.updateScore(user, emailName, scoreGroupName, googleAnalyticsVisitsDownloads.getUrl());
					if(logger.isDebugEnabled()) logger.debug("-----come back the Thread----");
					
					//Change the Status Type new to update
					googleAnalyticsVisitsDownloads.setStatus(Constants.GA_ACTION_UPDATE_TYPE);
					//googleAnalyticsVisitsDownloadsDao.saveOrUpdate(googleAnalyticsVisitsDownloads);
					googleAnalyticsVisitsDownloadsDaoForDML.saveOrUpdate(googleAnalyticsVisitsDownloads);

					
				}
				else if(googleAnalyticsVisitsDownloads.getAction().equalsIgnoreCase(Constants.GA_DOWNLOADED) && 
							googleAnalyticsVisitsDownloads.getStatus().equalsIgnoreCase(Constants.GA_ACTION_TYPE)) {
					scoreGroupName=Constants.SCORE_DOWNLOAD;
					
					if(logger.isDebugEnabled()) logger.debug("contactScoreSetting "+contactScoreSetting);
					if(logger.isDebugEnabled()) logger.debug("---ContactSCoreSettings updateScore Method called by downloaded----");
					contactScoreSetting.updateScore(user, emailName, scoreGroupName, googleAnalyticsVisitsDownloads.getUrl());
					if(logger.isDebugEnabled()) logger.debug("-----come back the Thread----");
					
					//Change the Status Type new to update
					googleAnalyticsVisitsDownloads.setStatus(Constants.GA_ACTION_UPDATE_TYPE);
					googleAnalyticsVisitsDownloadsDaoForDML.saveOrUpdate(googleAnalyticsVisitsDownloads);
				}
				else if(googleAnalyticsVisitsDownloads.getAction().equalsIgnoreCase(Constants.GA_SOURCEOFVISIT) && 
							googleAnalyticsVisitsDownloads.getStatus().equalsIgnoreCase(Constants.GA_ACTION_TYPE)) {
					if(logger.isDebugEnabled()) logger.debug("Sourceof visit EmailName is---------- :"+emailName +"source of visit action type is "
										+googleAnalyticsVisitsDownloads.getStatus());
					scoreGroupName=Constants.SCORE_SOURCE_OF_VISIT;
					
					if(logger.isDebugEnabled()) logger.debug("contactScoreSetting "+contactScoreSetting);
					if(logger.isDebugEnabled()) logger.debug("---ContactSCoreSettings updateScore Method called by sourceOfVisit---");
					contactScoreSetting.updateScore(user, emailName, scoreGroupName, googleAnalyticsVisitsDownloads.getSourceOfVisit());
					
					if(logger.isDebugEnabled()) logger.debug("-----come back the Thread----");
					//Change the Status Type new to update
					googleAnalyticsVisitsDownloads.setStatus(Constants.GA_ACTION_UPDATE_TYPE);
					googleAnalyticsVisitsDownloadsDaoForDML.saveOrUpdate(googleAnalyticsVisitsDownloads);
				}
				
			}
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception  : error while running the thred in GAVisitDownloaded"+e);
		}
		
	} //run
	
	
}
