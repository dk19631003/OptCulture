/**
 * Author			: Sudheer Mogullapally
 * Created Date 	: 20 April 2010 
 * Description		: Reads the SMTP Inbox(using IO Streams) and identifies any 
 * 					  bounce mails and records in the database.
 */

package org.mq.captiway.scheduler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.BouncesDao;
import org.mq.captiway.scheduler.dao.BouncesDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.BounceCategories;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;

public class BounceTracker extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public BounceTracker(){
		
	}
	
	/******************* Setters and Getters ****************/
	
	private BouncesDao bouncesDao;
	
	public BouncesDao getBouncesDao() {
		return bouncesDao;
	}

	public void setBouncesDao(BouncesDao bouncesDao) {
		this.bouncesDao = bouncesDao;
	}

	private BouncesDaoForDML bouncesDaoForDML;

	
	public BouncesDaoForDML getBouncesDaoForDML() {
		return bouncesDaoForDML;
	}

	public void setBouncesDaoForDML(BouncesDaoForDML bouncesDaoForDML) {
		this.bouncesDaoForDML = bouncesDaoForDML;
	}

	private CampaignsDao campaignsDao;

	public void setCampaignsDao(CampaignsDao campaignsDao) {
		this.campaignsDao = campaignsDao;
	}

	public CampaignsDao getCampaignsDao() {
		return this.campaignsDao;
	}

	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	private CampaignSentDao campaignSentDao;
	private CampaignSentDaoForDML campaignSentDaoForDML;
	public CampaignSentDaoForDML getCampaignSentDaoForDML() {
		return campaignSentDaoForDML;
	}

	public void setCampaignSentDaoForDML(CampaignSentDaoForDML campaignSentDaoForDML) {
		this.campaignSentDaoForDML = campaignSentDaoForDML;
	}

	public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
		this.campaignSentDao = campaignSentDao;
	}
	public CampaignSentDao getCampaignSentDao() {
		return this.campaignSentDao;
	}


	private ContactsDao contactsDao;
	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	public ContactsDao getContactsDao() {
		return this.contactsDao;
	}
	 private ContactsDaoForDML contactsDaoForDML;
	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}

	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	private SuppressedContactsDao suppressedContactsDao;
	public SuppressedContactsDao getSuppressedContactsDao() {
		return suppressedContactsDao;
	}
	public void setSuppressedContactsDao(SuppressedContactsDao suppressedContactsDao) {
		this.suppressedContactsDao = suppressedContactsDao;
	}
	
	private SuppressedContactsDaoForDML suppressedContactsDaoForDML;
	
	public SuppressedContactsDaoForDML getSuppressedContactsDaoForDML() {
		return suppressedContactsDaoForDML;
	}

	public void setSuppressedContactsDaoForDML(
			SuppressedContactsDaoForDML suppressedContactsDaoForDML) {
		this.suppressedContactsDaoForDML = suppressedContactsDaoForDML;
	}

	private UsersDao usersDao;
	public UsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	private CampaignReportDao campaignReportDao;
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	
	private CampaignReportDaoForDML campaignReportDaoForDML;
	

	public CampaignReportDaoForDML getCampaignReportDaoForDML() {
		return campaignReportDaoForDML;
	}

	public void setCampaignReportDaoForDML(
			CampaignReportDaoForDML campaignReportDaoForDML) {
		this.campaignReportDaoForDML = campaignReportDaoForDML;
	}

	private SpammedEmailTracker spammedEmailTracker = new SpammedEmailTracker();  
	
	private List<Bounces> bounceList = Collections.synchronizedList(new ArrayList<Bounces>());
	
	
	private final String VAR_X_RECEIVER = "x-receiver";
	private final String VAR_USER_ID = "userId";
	private final String VAR_CAMPAIGN_ID = "campaignId";
	private final String VAR_CAMPAIGN_REPORT_ID = "crId";
	private final String VAR_DIAGNOSTIC_CODE = "Diagnostic-Code";
	
	private final String VAR_SENT_ID = "sentId";
	
	//added newly inorder to identify from which application a particuler bounce file has prepared
	private final String VAR_MAIL_FROM_ID = "mailFromId";//need to enable 
	private final String VAR_MTA_CATEGORY = "X-PowerMTA-BounceCategory";
	private final String VAR_CONTENT = "senderContent";

	private final String[] headersArr = 
		{ VAR_X_RECEIVER, VAR_USER_ID, VAR_CAMPAIGN_REPORT_ID, VAR_DIAGNOSTIC_CODE,
					 VAR_CAMPAIGN_ID, VAR_SENT_ID, VAR_MTA_CATEGORY, VAR_CONTENT, VAR_MAIL_FROM_ID, "uid", "cid"};
	
	@Override
	public void run() {
		try {
			
			//added newly to not to let the test application do the bounce process
			String doStartBounceFlag = PropertyUtil.getPropertyValueFromDB("StartBounceProcess");
			if(doStartBounceFlag.equalsIgnoreCase("true")) {
				startBounce();
			}else {
				
				if(logger.isDebugEnabled()) logger.debug("do not start the bounce process....,returning");
				return;
			}
			//spammedEmailTracker.checkSpam(campaignSentDao);
			spammedEmailTracker.checkSpam(campaignSentDaoForDML);
		}
		finally {
			System.gc();
		}
	}
	
	public static void main(String[] args) {
		//new BounceTracker().startBounce();
		String applicationUrl = " http://localhost:8080";
		String targetUrl = "/Scheduler/updateBounceReport.mqrm?";
		String postData = "";
		try {
			URL url = new URL(applicationUrl+targetUrl);
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);
			
			
			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.flush();
			out.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		
		
	}
	
	
	void readAsAFile(File bounceFile, Map<String, String> headersMap) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(bounceFile));
			String lineStr;
			String value;
			
			while((lineStr=br.readLine()) != null) {
				lineStr=lineStr.trim();
				
				for (String headerStr : headersArr) {
					
					if(!lineStr.startsWith(headerStr+":")) {
						continue;
					}
					
					value = lineStr.substring(lineStr.indexOf(":")+1).trim();
					
					if(headerStr.equals("uid")) headerStr = VAR_USER_ID;
					if(headerStr.equals("cid")) headerStr = VAR_CAMPAIGN_ID;
					if(headerStr.equals(VAR_DIAGNOSTIC_CODE)) headerStr = VAR_CONTENT;
					
					if(!headersMap.containsKey(headerStr)) { 
						headersMap.put(headerStr, value);
					}
						
				} // for each
				if(headersMap.get(VAR_MTA_CATEGORY) == null) {
					
				}
				
			}// while
			
			br.close();
			
			if(logger.isDebugEnabled()) logger.debug("    >>>   "+ headersMap);
			
		} 
		catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("** Exception ", e);
		}
	} // readAsAFile
	
	private void startBounce() {
		
		try {
			if(logger.isInfoEnabled()) logger.info("<----------------------- just entered ------------------------->");
			
			String bounceInbox = PropertyUtil.getPropertyValue("bounceInbox");
			String processedDir = PropertyUtil.getPropertyValue("bounceProcessed");
			String unProcessedDir = PropertyUtil.getPropertyValue("bounceUnprocessed");
			
			if (bounceInbox == null) {
				if(logger.isWarnEnabled()) logger.warn(" Bounce Inbox directory is null ");
				return;
			}
			File inboxFile = new File(bounceInbox);
			if(!inboxFile.exists()) {
				if(logger.isWarnEnabled()) logger.warn("Bounce inbox directory does not exist");
				return;
			}
			
			File[] bounceArr = inboxFile.listFiles();
			
			if(bounceArr.length==0) {
				if(logger.isInfoEnabled()) logger.info("No bounce mails found in the inbox");
				return;
			}
			
			
			Map<String, String> headersMap = new HashMap<String, String>();
			Date bounceDate = new Date();
			
			if(logger.isInfoEnabled()) logger.info("Number of bonce files : "+ bounceArr.length);
			
			/**
			 * For Each file
			 */
			for (int i = 0; i < bounceArr.length; i++) {
				
					try {
						
						if(logger.isDebugEnabled()) logger.debug("Processing File : " + bounceArr[i].getName());
						
						if(bounceArr[i].canRead() == false)  	continue;
						
						headersMap.clear();
						readAsAFile(bounceArr[i], headersMap);

						if(doBoucneProcess(headersMap, bounceDate, null)) {
							bounceArr[i].renameTo(new File(processedDir+ File.separator+bounceArr[i].getName()));
						}
						else {
							bounceArr[i].renameTo(new File(unProcessedDir+ File.separator+bounceArr[i].getName()));
							if(logger.isWarnEnabled()) logger.warn(">>>>>>>>>>>>>>>> Unable to process the file: "+ bounceArr[i].getName());
						}
						
					} 
					catch (Exception e) {
						
						bounceArr[i].renameTo(new File(unProcessedDir+File.separator+bounceArr[i].getName()));
						if(logger.isErrorEnabled()) logger.error(" ** Exception :"+bounceArr[i].getName()+" while reading the bounce mail using stream", e);
					} 
			} // for
			if(bounceList.size() > 0) {
				saveByCollection(bounceList);
			}
			if(logger.isDebugEnabled()) logger.debug("-----------------------Exit -------------------------");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error(" ** Exception :Root:",e);
		}
		
		
	}
	
	/**
	 * The following String array contains tokens which are relates to type of bounce. By using these
	 *  the mail can be classified in to the appropriate category
	 */
	
	final String[] BOUNCE_TOKENS = {
			"MBF|ACCOUNT IS TEMPORARILY OVER QUOTA",
			"MBF|RECIPIENT STORAGE FULL", 
			"MBF|QUOTA EXCEEDED", 
			"MBF|OVER QUOTA|TEMPORARILY",
			"MBF|COULD NOT BE DELIVERED BECAUSE|IS FULL",

			"UND|THE REASON FOR THE PROBLEM|5.4.7|DELIVERY EXPIRED", 
			"UND|MESSAGE DELIVERY HAS BEEN DELAYED",
			"UND|MAIL SERVER|COULD NOT ACCEPT YOUR EMAIL AT THIS TIME",
			"UND|MAILBOX DISABLED|NOT ACCEPTING MESSAGES",

			"BLK|BLOCKED BY SPF",

			"NEA|DELIVERY TO THE FOLLOWING RECIPIENTS FAILED",
			"NEA|YOUR MESSAGE CANNOT BE DELIVERED TO THE FOLLOWING RECIPIENTS",
			"NEA|UNABLE TO DELIVER MESSAGE TO THE FOLLOWING RECIPIENTS",
			"NEA|YOUR MESSAGE WAS NOT DELIVERED|PLEASE VALIDATE RECIPIENT EMAIL ADDRESSAND TRY AGAIN",
			"NEA|DELIVERY FAILED",
			"NEA|NOT LISTED IN DOMINO DIRECTORY", 
			"NEA|USER UNKNOWN",
			"NEA|UNKNOWN OR ILLEGAL ALIAS",
			"NEA|THE RECIPIENT'S E-MAIL ADDRESS WAS NOT FOUND", 
			"NEA|THE FOLLOWING ADDRESSES HAD PERMANENT FATAL ERRORS",
			"NEA|UNKNOWN USER", 
			"NEA|5.1.0|UNKNOWN ADDRESS ERROR", 
			"NEA|YOUR MESSAGE COULD NOT BE DELIVERED",
			"NEA|SOME ADDRESSES WERE REJECTED",
			"NEA|MAILBOX UNAVAILABLE",
			"NEA|I WASN'T ABLE TO DELIVER YOUR MESSAGE TO THE FOLLOWING ADDRESSES",
			"NEA|NO MAILBOX HERE BY THAT NAME",

		};
	
	/**
	 * Finds out the bounce category by reading the content 
	 * @param senderContent
	 * @return Bounce category
	 */
	private String findBounceCategory(String senderContent) {
		
		if(senderContent == null) {
			return BounceCategories.OTHERS;
		}
		else {
			senderContent = senderContent.replace("\r\n", " ").replace("\n", " ").toUpperCase();
		}
		if(logger.isDebugEnabled()) {
			logger.debug("************* senderContent ******* \n" + senderContent);
		}

		String retTypeStr=null;
		for(int i=0; i<BOUNCE_TOKENS.length;i++) {
			
			retTypeStr = BOUNCE_TOKENS[i].substring(0,BOUNCE_TOKENS[i].indexOf("|"));
			
			
			if(isTokensFound(senderContent, BOUNCE_TOKENS[i].substring(BOUNCE_TOKENS[i].indexOf("|")+1))) {
				
				if(retTypeStr.equalsIgnoreCase("NEA"))	{
					return BounceCategories.NON_EXISTENT_ADDRESS;
				}
				else if(retTypeStr.equalsIgnoreCase("UND"))	{
					return BounceCategories.UNDELIVERABLE;
				}
				else if(retTypeStr.equalsIgnoreCase("MBF"))	{
					return BounceCategories.MAILBOX_FULL;
				}
				else if(retTypeStr.equalsIgnoreCase("BLK"))	{
					return BounceCategories.BLOCKED;
				}
				
			} // if
		} // for
		return BounceCategories.OTHERS; 

	} // findBounceCategory

	
	private boolean isTokensFound(String senderContent, String completeStr) {
		
		String[] tokesArr =  completeStr.split("\\|");
		for (String token : tokesArr) {
			if(!senderContent.contains(token.trim())) {
				return false;
			}
		} // for
		return true;
	}
		
	private boolean doBoucneProcess(Map<String, String> headersMap, Date bouncedTime, String str) {
		
		String sentId = null;
		String userId = null;
		String campaignId = null;
		String crId = null;
		String category = null;
		//String applicationUrl = PropertyUtil.getPropertyValue("ApplicationIp");
		String targetUrl = "/Scheduler/updateBounceReport.mqrm?";
		String mailFromId = null;
		String postData = "";
		String applicationUrl="";
		
		String tempStr = null;
		
			if(headersMap.get(VAR_SENT_ID) == null) {
				
				tempStr = headersMap.get(VAR_X_RECEIVER);
				if(tempStr == null) {
					if(logger.isWarnEnabled()) logger.warn("Even the header x-receiver not found ");
					return false;
				}
				sentId = tempStr.substring(tempStr.indexOf('-')+1, tempStr.indexOf('@'));
				//sentId = Long.parseLong(tempStr);
			}
			else {
				tempStr = headersMap.get(VAR_SENT_ID);
				if(tempStr.contains("-")) {
					tempStr = tempStr.substring(tempStr.lastIndexOf('-')+1).trim();
				}
				sentId = tempStr;
			}
			postData += "sentId="+sentId;
		
		if(headersMap.get(VAR_CAMPAIGN_REPORT_ID) != null) {
			crId = headersMap.get(VAR_CAMPAIGN_REPORT_ID);
			postData += "&crId="+crId;
		}
		else {
			//crId = campaignSentDao.getCrIdBySentId(sentId); 	  // TODO: Commented code to avoid bounces from other CRIDs
		}
		if(crId == null) {
			if(logger.isWarnEnabled()) logger.warn(" ---- crId not found for the sentId :"+sentId);
			return false;
		}
		
		
		userId = headersMap.get(VAR_USER_ID);
		if(userId == null) {
			userId = (campaignReportDao.getUserIdByCrId(Long.parseLong(crId))).toString();
		}
		postData += "&userId="+userId;
		
		if(headersMap.get(VAR_MTA_CATEGORY) == null) {
//			logger.debug(" senderContent " + headersMap.get(VAR_CONTENT));
			headersMap.put(VAR_MTA_CATEGORY, findBounceCategory(headersMap.get(VAR_CONTENT)));
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(" Sent Id  :"+sentId+", Cr Id :"+crId+
					", User Id" +userId+", Category :"+headersMap.get(VAR_MTA_CATEGORY));
		}
		postData += "&category="+headersMap.get(VAR_MTA_CATEGORY);
		//need to ping the appropriate application based on the header value of 'mailFromId'
		
		String message = headersMap.get(VAR_CONTENT) == null ?"MAIL IS BOUNCED" +
				"(No message from the Recipient SMTP Server)":headersMap.get(VAR_CONTENT);
		
		postData += "&message="+message;
		
		if(headersMap.get(VAR_MAIL_FROM_ID) == null) {
			if(logger.isDebugEnabled()) logger.debug("no mailFromId has found...returning");
			return false;
			
		}// if
		else {
			
			mailFromId = headersMap.get(VAR_MAIL_FROM_ID);
			if(mailFromId.equalsIgnoreCase("app")) {
				
				applicationUrl = "http://app.captiway.com";
				
				
			}//if
			else if(mailFromId.equalsIgnoreCase("test")) {
				
				applicationUrl = "http://test.captiway.com";
				
			}else if(mailFromId.equalsIgnoreCase("localhost")) {
				
				
				applicationUrl = "http://localhost:8080";
			}
			if(logger.isDebugEnabled()) logger.debug("applicationUrl :: "+applicationUrl);
			try {
				URL url = new URL(applicationUrl+targetUrl);
				if(logger.isDebugEnabled()) logger.debug("url "+ url);
				HttpURLConnection urlconnection = (HttpURLConnection)url.openConnection();
				if(logger.isDebugEnabled()) logger.debug("url conn"+ urlconnection);
				urlconnection.setRequestMethod("POST");
				urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlconnection.setDoOutput(true);
				
				
				OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(postData);
				//logger.debug("closing ::"+postData);
				out.flush();
				out.close();
				String response = null;
				BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				
				String decodedString;
				while ((decodedString = in.readLine()) != null) {
					response += decodedString;//response content
				}
				in.close();
				if(logger.isDebugEnabled()) logger.debug("response is======>"+response);
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			} catch (Exception e) {
				logger.error("Exception ::::" , e);
				return false;
			}
			
		}// else
		
		return true;
		
	}
	
	
	
	
	
	
	/**
	 * Inserts the bounce record in the database, update the bounce count in CampaignReport table
	 * and changes the status of sentId in CampaignSent as Bounced
	 * @param userIdStr
	 * @param campaignIdStr
	 * @param sentIdStr
	 * @param emailId
	 * @param message
	 * @param bouncedTime
	 * @param category
	 */
	private boolean doBoucneProcess(Map<String, String> headersMap, Date bouncedTime) {
		
		Long sentId = null;
		Long userId = null;
		Long campaignId = null;
		Long crId = null;
		String category = null;
		try {
			String tempStr = null;
			try {
				if(headersMap.get(VAR_SENT_ID) == null) {
					
					tempStr = headersMap.get(VAR_X_RECEIVER);
					if(tempStr == null) {
						if(logger.isWarnEnabled()) logger.warn("Even the header x-receiver not found ");
						return false;
					}
					tempStr = tempStr.substring(tempStr.indexOf('-')+1, tempStr.indexOf('@'));
					sentId = Long.parseLong(tempStr);
				}
				else {
					tempStr = headersMap.get(VAR_SENT_ID);
					if(tempStr.contains("-"))
							tempStr = tempStr.substring(tempStr.lastIndexOf('-')+1).trim();
					sentId = Long.parseLong(tempStr);
				}
			} catch (NumberFormatException e) {
				logger.error(" ** Exception : while parsing the sentId "+tempStr, e);
				return false;
			}
			
			if(headersMap.get(VAR_CAMPAIGN_REPORT_ID) != null) {
				crId = Long.parseLong(headersMap.get(VAR_CAMPAIGN_REPORT_ID));
			}
			else {
				//crId = campaignSentDao.getCrIdBySentId(sentId); 	  // TODO: Commented code to avoid bounces from other CRIDs
			}
			if(crId == null) {
				if(logger.isWarnEnabled()) logger.warn(" ---- crId not found for the sentId :"+sentId);
				return false;
			}
			
			try {
				userId = Long.parseLong(headersMap.get(VAR_USER_ID));
			} catch (Exception e1) {
				userId = campaignReportDao.getUserIdByCrId(crId);
			}
			
			
			if(headersMap.get(VAR_MTA_CATEGORY) == null) {
//				logger.debug(" senderContent " + headersMap.get(VAR_CONTENT));
				headersMap.put(VAR_MTA_CATEGORY, findBounceCategory(headersMap.get(VAR_CONTENT)));
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug(" Sent Id  :"+sentId+", Cr Id :"+crId+
						", User Id" +userId+", Category :"+headersMap.get(VAR_MTA_CATEGORY));
			}
			
			int rowsAffected = 0;
			
			try {
				campaignId = Long.parseLong(headersMap.get(VAR_CAMPAIGN_ID));
			} catch (NumberFormatException e) {
				logger.warn(" Campaign Id not found in the headers list");
			}
			category = headersMap.get(VAR_MTA_CATEGORY);

			String message = headersMap.get(VAR_CONTENT) == null ?"MAIL IS BOUNCED" +
					"(No message from the Recipient SMTP Server)":headersMap.get(VAR_CONTENT);
			
			CampaignSent campaignSent = null;
			
			if(logger.isDebugEnabled())	
				logger.debug("User Id : " + userId+ " campaign ID : " + campaignId+ " sentId : " + sentId);
			
			//rowsAffected = campaignSentDao.setStatusBySentId(sentId, Constants.CS_STATUS_BOUNCED, crId);
			rowsAffected = campaignSentDaoForDML.setStatusBySentId(sentId, Constants.CS_STATUS_BOUNCED, crId);
			
			if(logger.isDebugEnabled())
				logger.debug(" No of rows affected while updating the campaign sent : "+rowsAffected);
			if(rowsAffected <= 0) {
				logger.warn(" No rows updated in the database when trying for sentId to update as Bounce :"+sentId);
				return false;
				//TODO need to Verify the sent_id in the Bounce table.
			}
			if(logger.isDebugEnabled()) 
				logger.debug("Bounce status is updated for the Sent Id : "+sentId);
			
			//rowsAffected = campaignReportDao.updateReport(crId, Constants.CR_TYPE_BOUNCES, 1);
			rowsAffected = campaignReportDaoForDML.updateReport(crId, Constants.CR_TYPE_BOUNCES, 1);
			if(!(rowsAffected > 0)){
				if(logger.isWarnEnabled()) logger.warn(" CampaignReport with Id - "+crId+" bounce updation failed for sent Id : "+
						sentId);
			}
			
			campaignSent = campaignSentDao.findById(sentId);
			if(bounceList.size() >= 30) {
				saveByCollection(bounceList);
			}
			
			String tempCat = BounceCategories.categories.get(category.trim().toLowerCase());
			if(tempCat==null) {
				tempCat = BounceCategories.categories.get("others");
			}
			
			String catStr = tempCat + "/" + category;
			bounceList.add(new Bounces(campaignSent, catStr, message, bouncedTime, crId));
			inactiveContact(userId, crId, campaignSent.getEmailId(), category);
			
			return true;
		} 
		catch (NumberFormatException e) {
			logger.error(" Exception : NumberFormatException", e);
			return false;
		}
		catch(Exception e) {
			logger.error(" Exception :", e);
			return false;
		}
	}
	
	private void saveByCollection(Collection<Bounces> bounceList){
		try {
			//bouncesDao.saveByCollection(bounceList);
			bouncesDaoForDML.saveByCollection(bounceList);

			bouncesDaoForDML.clear(bounceList);
			bounceList.clear();
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
	}
	
	private void inactiveContact(Long userId, Long crId, String emailId, String category) {
		
		List<String> statusList = null;
		boolean isSuccess = false;
		int size = 0;
		int minConsideredCount = 0;
		try {
			category = BounceCategories.categories.get(category);
	
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>>>Category  "+ category);
			}
			
			if(category.equals(BounceCategories.NON_EXISTENT_ADDRESS)) {
				
				contactsDaoForDML.updateContactStatus(emailId, userId, Constants.CS_STATUS_BOUNCED);
				addToSuppressedContacts(userId, emailId);
			}
			else if(category.equals(BounceCategories.UNDELIVERABLE)) {
				
				statusList = campaignSentDao.getRecentStatusForEmailId(crId, emailId); //3
				size = statusList.size() < 3 ? statusList.size() : 3;
				minConsideredCount = 2;
			} 
			else if(category.equals(BounceCategories.MAILBOX_FULL)) {
				
				statusList = campaignSentDao.getRecentStatusForEmailId(crId, emailId); // 4		
				size = statusList.size() < 4 ? statusList.size() : 4;
				minConsideredCount = 3;
			}
			if(statusList != null ) {
				
				for (int i = 0; i < size; i++) {
					
					if(statusList.get(i).equalsIgnoreCase(Constants.CS_STATUS_SUCCESS)) {
						isSuccess = true;
						break;
					}
				}
				if(!isSuccess && statusList.size() > minConsideredCount) {
					
					contactsDaoForDML.updateContactStatus(emailId, userId, Constants.CS_STATUS_BOUNCED);
					addToSuppressedContacts(userId, emailId);
				}
			}
			
		}catch(Exception e) {
			logger.error("**Exception : while inactivating the contact ", e);
		}
	}
	
	public void addToSuppressedContacts(Long userId, String emailId) {
		try {
			SuppressedContacts suppressedContact = new SuppressedContacts();
			Users users = usersDao.find(userId); 
			suppressedContact.setUser(users);
			suppressedContact.setEmail(emailId);
			suppressedContact.setType(Constants.SUPP_TYPE_BOUNCED);
			suppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to suppress contacts .");
		} catch (Exception e) {
			logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	
}
