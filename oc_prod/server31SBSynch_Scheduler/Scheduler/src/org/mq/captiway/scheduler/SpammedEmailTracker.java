/**
 * Author		: Sudheer Mogullapally
 * Created Date	: 19 April 2010
 * Description	: This class is helpful to track the spam report emails and
 * 				  store the details in the database.
 * 
 */

package org.mq.captiway.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;


public class SpammedEmailTracker {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	/**
	 * Reads the mails in Inbox by using IO Streams and identifies any spam reports.
	 * If any spam reports find, records the details in the database. 
	 */
	
	private String VAR_X_RECEIVER = "x-receiver";
	private String VAR_TO = "To:";
	private String VAR_CRID = "crId:";
	private String VAR_SENT_ID = "sentId:";
	
	public void checkSpam(/*CampaignSentDao campaignSentDao*/CampaignSentDaoForDML campaignSentDaoForDML) {
		
		if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>> just entered <<<<<<<");
		}
        try {
        	
        	String spamDir = PropertyUtil.getPropertyValue("spamDirectory");  
        	if(spamDir == null) {
        		if(logger.isWarnEnabled()) logger.warn(" Spam Directory is not found in application.properties file ");
        		return;
        	}
        	
        	String spamProcessedDir = PropertyUtil.getPropertyValue("spamProcessedDir");
        	String spamUnprocessedDir = PropertyUtil.getPropertyValue("spamUnprocessedDir");
        	if(spamProcessedDir == null) {
        		if(logger.isWarnEnabled()) logger.warn(" Spam Proccessed directory information is not found in application.properties file");
        		return;
        	}
        	
        	File inbox = new File(spamDir);
			
        	if(!inbox.exists()) {
        		if(logger.isWarnEnabled()) logger.warn("Spam inbox does not exist");
        		return;
        	}
			
			String[] spamReportStr = {"EMAIL ABUSE REPORT"};
			File[] spamFiles = inbox.listFiles();
			
			if(spamFiles.length==0) {
				if(logger.isInfoEnabled()) logger.info("No Spam files founce in the inbox");
				return;
			}
			
			//String sentIdsStr = "";
			String sentId = null;
			String crId =null;
			BufferedReader br = null;
			String lineStr = null; 
			StringBuffer contentSb = null;
			boolean isSpam = false;
			
			for (int i = 0; i < spamFiles.length; i++) {
				
				try {
					isSpam = false;
					crId = null;
					sentId = null;
					contentSb = null;
					contentSb = new StringBuffer();
					
					br = new BufferedReader(new FileReader(spamFiles[i]));
					
					while((lineStr=br.readLine())!=null) {
						contentSb.append(Utility.unescape(lineStr).toUpperCase() + " ");
						if(lineStr.trim().startsWith(VAR_X_RECEIVER) && lineStr.contains("postmaster@")) {
							isSpam = true;
						}
						else if(lineStr.trim().startsWith(VAR_TO) && lineStr.contains("postmaster@")) {
							isSpam = true;
						}
						if(lineStr.contains(VAR_SENT_ID) && sentId == null) {
							if(lineStr.startsWith(VAR_SENT_ID))
								sentId = Utility.unescape(lineStr);
						}
						if(crId ==null && lineStr.startsWith(VAR_SENT_ID)) { 
							String tempStr = Utility.unescape(lineStr);
							crId = tempStr.substring(tempStr.indexOf(':')+1).trim();
						}
					} // while
					
					br.close();
					
					if(!isSpam || sentId == null) {
						if(logger.isWarnEnabled()) logger.warn("Not a spam file : " + spamFiles[i].getName());
						spamFiles[i].renameTo(
								new File(spamUnprocessedDir + File.separator+spamFiles[i].getName()));
						continue;
					}
					
					for (int j = 0; j < spamReportStr.length; j++) {
						if(contentSb.indexOf(spamReportStr[j])>0) {
							isSpam = true;
							break;
						}
					}// for j
					
					if(!isSpam) { 
						if(logger.isWarnEnabled()) logger.warn("Spam related content not found in the file : " + spamFiles[i].getName());
						spamFiles[i].renameTo(
								new File(spamUnprocessedDir + File.separator+spamFiles[i].getName()));
						continue;
					}
					
					if(logger.isDebugEnabled()) logger.debug("sentId header:"+sentId);
					
					if(sentId.indexOf('-')>0) {
						sentId = sentId.substring(sentId.lastIndexOf('-')+1).trim();
					} else {
						sentId = sentId.substring(sentId.lastIndexOf(':')+1).trim();
					}
					if(logger.isDebugEnabled()) logger.debug("sentId : " + sentId);
					
					//sentIdsStr += ((sentIdsStr.length()!=0)?"," : "") + sentId;
					
					
					spamFiles[i].renameTo(
							new File(spamProcessedDir+File.separator+spamFiles[i].getName()));
					
					//logger.info(" Sent Ids reported as spam :"+sentIdsStr);
					
					if(sentId.length()==0 || crId==null || crId.length()==0) {
						if(logger.isErrorEnabled()) logger.error("** Unable to get  sentId="+sentId+"   Or  crId="+crId);
						continue;
					}
					
					//int updatedCount = campaignSentDao.updateAsSpamBySentId(sentId, crId);
					int updatedCount = campaignSentDaoForDML.updateAsSpamBySentId(sentId, crId);
					
					if(logger.isInfoEnabled()) logger.info(" No of updated sentIds as Spammed :"+updatedCount);

				} catch (Exception e) {
					if(logger.isErrorEnabled()) logger.error("** Exception :", e);
				}
	        	
			} // for i
			
        } catch (Exception e) {
           if(logger.isErrorEnabled()) logger.error("** Exception :", e);
        }

        if(logger.isInfoEnabled()) logger.info(" >>>>>>> Exiting the spam tracker <<<<<<<");
		
	}
	
	
	
	
	//overrided for updating the spam count in reports
public void checkSpam(/*CampaignSentDao campaignSentDao*/CampaignSentDaoForDML campaignSentDaoForDML, /*CampaignReportDao campaignReportDao*/CampaignReportDaoForDML campaignReportDaoForDML) {
		
		if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>> just entered <<<<<<<");
		}
        try {
        	
        	String spamDir = PropertyUtil.getPropertyValue("spamDirectory");  
        	if(spamDir == null) {
        		if(logger.isWarnEnabled()) logger.warn(" Spam Directory is not found in application.properties file ");
        		return;
        	}
        	
        	String spamProcessedDir = PropertyUtil.getPropertyValue("spamProcessedDir");
        	String spamUnprocessedDir = PropertyUtil.getPropertyValue("spamUnprocessedDir");
        	if(spamProcessedDir == null) {
        		if(logger.isWarnEnabled()) logger.warn(" Spam Proccessed directory information is not found in application.properties file");
        		return;
        	}
        	
        	File inbox = new File(spamDir);
			
        	if(!inbox.exists()) {
        		if(logger.isWarnEnabled()) logger.warn("Spam inbox does not exist");
        		return;
        	}
			
			String[] spamReportStr = {"EMAIL ABUSE REPORT"};
			File[] spamFiles = inbox.listFiles();
			
			if(spamFiles.length==0) {
				if(logger.isInfoEnabled()) logger.info("No Spam files founce in the inbox");
				return;
			}
			
			//String sentIdsStr = "";
			String sentId = null;
			String crId =null;
			BufferedReader br = null;
			String lineStr = null; 
			StringBuffer contentSb = null;
			boolean isSpam = false;
			
			for (int i = 0; i < spamFiles.length; i++) {
				
				try {
					isSpam = false;
					crId = null;
					sentId = null;
					contentSb = null;
					contentSb = new StringBuffer();
					
					br = new BufferedReader(new FileReader(spamFiles[i]));
					
					while((lineStr=br.readLine())!=null) {
						contentSb.append(Utility.unescape(lineStr).toUpperCase() + " ");
						if(lineStr.trim().startsWith(VAR_X_RECEIVER) && lineStr.contains("postmaster@")) {
							isSpam = true;
						}
						else if(lineStr.trim().startsWith(VAR_TO) && lineStr.contains("postmaster@")) {
							isSpam = true;
						}
						if(lineStr.contains(VAR_SENT_ID) && sentId == null) {
							if(lineStr.startsWith(VAR_SENT_ID))
								sentId = Utility.unescape(lineStr);
						}
						if(crId ==null && lineStr.startsWith(VAR_SENT_ID)) { 
							String tempStr = Utility.unescape(lineStr);
							crId = tempStr.substring(tempStr.indexOf(':')+1).trim();
						}
					} // while
					
					br.close();
					
					if(!isSpam || sentId == null) {
						if(logger.isWarnEnabled()) logger.warn("Not a spam file : " + spamFiles[i].getName());
						spamFiles[i].renameTo(
								new File(spamUnprocessedDir + File.separator+spamFiles[i].getName()));
						continue;
					}
					
					for (int j = 0; j < spamReportStr.length; j++) {
						if(contentSb.indexOf(spamReportStr[j])>0) {
							isSpam = true;
							break;
						}
					}// for j
					
					if(!isSpam) { 
						if(logger.isWarnEnabled()) logger.warn("Spam related content not found in the file : " + spamFiles[i].getName());
						spamFiles[i].renameTo(
								new File(spamUnprocessedDir + File.separator+spamFiles[i].getName()));
						continue;
					}
					
					if(logger.isDebugEnabled()) logger.debug("sentId header:"+sentId);
					
					if(sentId.indexOf('-')>0) {
						sentId = sentId.substring(sentId.lastIndexOf('-')+1).trim();
					} else {
						sentId = sentId.substring(sentId.lastIndexOf(':')+1).trim();
					}
					if(logger.isDebugEnabled()) logger.debug("sentId : " + sentId);
					
					//sentIdsStr += ((sentIdsStr.length()!=0)?"," : "") + sentId;
					
					
					spamFiles[i].renameTo(
							new File(spamProcessedDir+File.separator+spamFiles[i].getName()));
					
					//logger.info(" Sent Ids reported as spam :"+sentIdsStr);
					
					if(sentId.length()==0 || crId==null || crId.length()==0) {
						if(logger.isErrorEnabled()) logger.error("** Unable to get  sentId="+sentId+"   Or  crId="+crId);
						continue;
					}
					
					//int updatedCount = campaignSentDao.updateAsSpamBySentId(sentId, crId);
					int updatedCount = campaignSentDaoForDML.updateAsSpamBySentId(sentId, crId);
					
					if(logger.isInfoEnabled()) logger.info(" No of updated sentIds as Spammed :"+updatedCount);

				} catch (Exception e) {
					if(logger.isErrorEnabled()) logger.error("** Exception :", e);
				}
				
				
				//CampaignReport campaignReport = campaignSent.getCampaignReport();
				try {
					//campaignReportDao.updateCampaignReport(crId, sentId, Constants.CR_TYPE_SPAM);
					campaignReportDaoForDML.updateCampaignReport(crId, sentId, Constants.CR_TYPE_SPAM);
				} catch (Exception e) {
					if(logger.isErrorEnabled()) logger.error("Exception : Problem while updating the opens in CampaignReport \n", e);
				}
				
	        	
			} // for i
			
        } catch (Exception e) {
           if(logger.isErrorEnabled()) logger.error("** Exception :", e);
        }

        if(logger.isInfoEnabled()) logger.info(" >>>>>>> Exiting the spam tracker <<<<<<<");
		
	}
	
	
	
	
	
	
	
	
}
