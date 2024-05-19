package org.mq.captiway.scheduler.services;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Provider;
import javax.mail.Provider.Type;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.SharedByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.captiway.scheduler.PmtaMailmergeSubmitter;
import org.mq.captiway.scheduler.RecipientProvider;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.NotSentToContacts;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.beans.Vmta;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.NotSentToContactsDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.ContactPHValue;
import org.mq.captiway.scheduler.utility.ContactPhValuesEnum;
import org.mq.captiway.scheduler.utility.EncryptDecryptUrlParameters;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PrepareFinalHTML;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;

import com.sun.mail.smtp.SMTPMessage;


/**
 * 
 * @author vinay
 *	This class enables us to send through sendGridAPI in a multi threaded fashion .
 *  No. of threads is configurable from DB( application_properties TABLE) .
 *   
 */

public class ExternalSMTPSender {
	
	//Existing Code
	 public static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	 public  String SMTP_AUTH_USER;
	 public  String SMTP_AUTH_PWD;

	 public  String SMTP_SINGLE_AUTH_USER;
	 public  String SMTP_SINGLE_AUTH_PWD;

	 //New Implementation.
	 /**
	  * Here we need to fetch, which VMTA is assigned to the user.
	  * VMTA Will give details
	  * 1.SMTP Gateway details
	  * 2.Account details
	  * 3.Password
	  * 4.IP (Which account we are using)
	  */
	 
	 
	 
	 public static Properties props;
	 
	 public ExternalSMTPSender() {}
	 
	 /**
	 * How many threads to create.
	 */
	 private static int NUMBER_OF_THREADS;
	 
	 private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	

	 private ApplicationContext context;
	 
	 //why static is required ?
	 private static  Map<String, OrganizationStores > OrgStoreMap = null;
	 private  static Map<String, String > OrgStoreAddressMap = null;
	 private static String userAddress = null;
	 
	 
	public ExternalSMTPSender(Vmta vmta, ApplicationContext context) {
	logger.debug("<<<<< Completed ExternalSMTPSender vmta.");
		
		try {
			this.context = context;
			/*SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
			SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);*/
			SMTP_AUTH_USER = vmta.getAccountName();
			SMTP_AUTH_PWD = vmta.getAccountPwd();
			logger.info("In Constructor 1 SMTP_AUTH_USER ::"+SMTP_AUTH_USER +" SMTP_AUTH_PWD:: "+SMTP_AUTH_PWD);
			SMTP_SINGLE_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID);
			SMTP_SINGLE_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD);
			
			OrgStoreMap = null;
			OrgStoreAddressMap = null;
			userAddress = null;
			
			 /*props = new Properties();
			 props.put("mail.transport.protocol", "smtp");
			 props.put("mail.smtp.host", SMTP_HOST_NAME);
			 props.put("mail.smtp.port", 587);
			 props.put("mail.smtp.auth", "true");*/
			//Added in 2.3.11
			 props = new Properties();
			 props.put(Constants.SMTP_KEY_PROTOCOL, Constants.SMTP_VALUE_PROTOCOL);
			 props.put(Constants.SMTP_KEY_HOST, Constants.SMTP_VALUE_HOST);
			 props.put(Constants.SMTP_KEY_PORT, Integer.parseInt(Constants.SMTP_VALUE_PORT));
			 props.put(Constants.SMTP_KEY_AUTH, Constants.SMTP_VALUE_AUTH);
			 
			 NUMBER_OF_THREADS = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_THREAD_COUNT));
			 
			 
		} catch (Exception e) {
			logger.error("** Error Property is not defined : "+Constants.PROPS_KEY_SENDGRID_THREAD_COUNT);
			NUMBER_OF_THREADS = 10;
		}
		logger.debug(">>>>>>> Started  ExternalSMTPSender :vmta: ");
	}
	
	public ExternalSMTPSender(ApplicationContext context) {
		logger.debug(">>>>>>> Started  ExternalSMTPSender :context: ");
		this.context = context;
		
		try {
			logger.info("In Constructor  2 SMTP_AUTH_USER ::"+SMTP_AUTH_USER +" SMTP_AUTH_PWD:: "+SMTP_AUTH_PWD);
			SMTP_SINGLE_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID);
			SMTP_SINGLE_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD);
			
			OrgStoreMap = null;
			OrgStoreAddressMap = null;
			userAddress = null;
			
			 props = new Properties();
			 props.put("mail.transport.protocol", "smtp");
			 props.put("mail.smtp.host", SMTP_HOST_NAME);
			 props.put("mail.smtp.port", 587);
			 props.put("mail.smtp.auth", "true");
			 
			 NUMBER_OF_THREADS = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_THREAD_COUNT));
			 
			 
		} catch (Exception e) {
			logger.error("** Error Property is not defined : "+Constants.PROPS_KEY_SENDGRID_THREAD_COUNT);
			NUMBER_OF_THREADS = 10;
		}
		logger.debug("<<<<< Completed ExternalSMTPSender context.");
	}

	//added for substitution tags
	public static String getStoreAddress(String posLocId, boolean isAddressFlag) {
		String retVal = Constants.STRING_NILL;
		
		
		if(posLocId == null || OrgStoreAddressMap == null ){
			
			if( !isAddressFlag )return retVal;
			
			else return userAddress;
			
			
		}
		
		String address = OrgStoreAddressMap.get(posLocId);
		
		if(address == null) {
			if( !isAddressFlag )return retVal;
			
			else return userAddress;
		}
		
		return address;
		
	}//getStoreAddress
	
	
	//added for substitution tags
	public static String getStorePlaceholder(String posLocId, String placeholder, ContactPHValue contactPHValue, String defVal) {
		
		String retVal = Constants.STRING_NILL;
		if(posLocId == null || OrgStoreMap == null) return retVal;
		
		OrganizationStores organizationStores = OrgStoreMap.get(posLocId);
		
		if(organizationStores == null) return retVal;
		
		if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORENAME.toLowerCase())) { 
			
			retVal =  organizationStores.getStoreName() ;
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreName(retVal);
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREMANAGER.toLowerCase())){
			retVal = organizationStores.getStoreManagerName();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreManager(retVal);
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREEMAIL.toLowerCase())){
			retVal = organizationStores.getEmailId();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreEmail(retVal);
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREPHONE.toLowerCase())){
			retVal = organizationStores.getAddress().getPhone();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStorePhone(retVal);
			
			
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORESTREET.toLowerCase())){
			retVal = organizationStores.getAddress().getAddressOne();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreStreet(retVal);
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORECITY.toLowerCase())){
			retVal = organizationStores.getAddress().getCity();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreCity(retVal);
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STORESTATE.toLowerCase())){
			retVal = organizationStores.getAddress().getState();
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreState(retVal);
			
		}
		else if(placeholder.toLowerCase().endsWith(PlaceHolders.CAMPAIGN_PH_STOREZIP.toLowerCase())){
			retVal = organizationStores.getAddress().getPin() != null ? organizationStores.getAddress().getPin()+Constants.STRING_NILL:Constants.STRING_NILL;
			
			retVal = (retVal == null ? defVal : retVal);
			contactPHValue.setStoreZip(retVal);
		}
		
		if(retVal != null) return retVal;
		else return defVal;
		
	}//getStorePlaceholder
	
	
	
	
	
	
	
	/**
	 * 
	 * @param mailingList
	 * @param totalCount
	 * @param contentStr
	 * @return boolean flag for success (true) or failure (false)
	 *  This method is for submitting double optin emails.
	 */
	public boolean submitDoubleOptinEmails(MailingList mailingList, int totalCount, String contentStr, CustomTemplates customTemplate) {

		
		//logger.info(" --- just entered---");
		try{
			String supportEmailStr = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);

			//replace the senderName in the content
			/*String senderName = (mailingList.getUsers().getFirstName() != null ) ?
					mailingList.getUsers().getFirstName() : mailingList.getUsers().getUserName();
					if(logger.isDebugEnabled()) {
						logger.info(">>>>>>>>>> senderName to replace as :"+senderName);
					}
*/

					Set<String> totalPhSet = PmtaMailmergeSubmitter.getTotalPhSet();
					
					if(logger.isDebugEnabled()) logger.debug("totalPhSet <<<<<<<<< "+ totalPhSet);
					
					//htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
					//textContent = textContent.replace("|^", "[").replace("^|", "]");
					
					String subject = "Confirmation Request";
					Users mlUser = mailingList.getUsers();
					/*if(mlUser.getParentUser() != null) {
						
						mlUser = mlUser.getParentUser();
					}*/
					
					//replace the senderName in the content
					String senderName = (mlUser.getFirstName() != null ) ?
							mlUser.getFirstName() : mlUser.getUserName();
							/*if(logger.isDebugEnabled()) {
								logger.info(">>>>>>>>>> senderName to replace as :"+senderName);
							}*/
		
					
					contentStr = contentStr.replace("[senderName]", senderName);
					
					String senderEmail = null;
					if(mlUser.getEmailId()!=null && mlUser.getEmailId().trim().length() > 0)  {
						
						senderEmail = mlUser.getUserOrganization().getOrganizationName()+"<"+mlUser.getEmailId()+">";
						SENDER_EMAIL = senderEmail;
					}
					else{
						
						senderEmail = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
						SENDER_EMAIL = "OptCulture Support"+"<"+senderEmail+">";// 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place1
					}
					
					//SENDER_EMAIL =  senderEmail;
					SENDER_REPLY_TO_EMAIL = senderEmail;
					
					
					if(customTemplate != null) {
						
						SENDER_EMAIL = customTemplate.getFromName() + "<"+customTemplate.getFromEmail()+">";
						
						subject = (customTemplate.getSubject() == null || customTemplate.getSubject().trim().equals(Constants.STRING_NILL)) ? 
								subject : customTemplate.getSubject();
						
						subject = subject.replace("|^", "[").replace("^|", "]");
						
						Set personTagsSet = getSubjectCustomFields(subject);
						if(personTagsSet != null && personTagsSet.size() > 0) {
							
							
							if(totalPhSet == null) {
								//totalPhSet = new HashSet<String>();
								totalPhSet = personTagsSet;
							} else {
								totalPhSet.addAll(personTagsSet);
							}	
						}
						
					}

					
					contentStr = contentStr.replace("[url]", PropertyUtil.getPropertyValue("confirmOptinUrl"));
					//logger.info("content Str is===>"+contentStr);
					
					
					//************* Code to replace symbols and Date of email campaign*****************
					Set<String> symbolSet = getSubjectSymbolAndDateFields(contentStr);
					if(symbolSet != null && symbolSet.size()>0){
						for (String symbol : symbolSet) {
								 if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
									if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
										Calendar cal = MyCalendar.getNewCalendar();
										contentStr = contentStr.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									}
									else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
										Calendar cal = MyCalendar.getNewCalendar();
										cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
										contentStr = contentStr.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									}
									else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
										
										try {
											String[] days = symbol.split("_");
											Calendar cal = MyCalendar.getNewCalendar();
											cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
											contentStr = contentStr.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
										} catch (Exception e) {
											logger.debug("exception in parsing date placeholder");
										}
									}
								}
						}
					}
					
					//************* END**********************************************************************
					
					if(logger.isDebugEnabled()) {
						//logger.info(">>>>>>>>>> contentStr after all replacements :"+contentStr);
					}

					
					RecipientProvider recipientProvider = new RecipientProvider(context,mailingList, totalCount, totalPhSet);
					
									
					// Create threads to submit campaign
					List<Thread> threadsList = new ArrayList<Thread>();
					
					try {
						for(int i=0;i < NUMBER_OF_THREADS;i++) {
							Thread th = new MultiThreadedSubmission(recipientProvider, mailingList, contentStr, 
									Constants.SOURCE_DOUBLEOPTIN, customTemplate, context, totalPhSet, SMTP_AUTH_USER, SMTP_AUTH_PWD);
							th.setName("thread:" + i);
							threadsList.add(th);
							th.start();
						}
					} catch (Exception e) {
						logger.error("Exception: Error occured while creating threads ",e);
					}
					
					// wait for all threads to finish
					Iterator<Thread> iter;
					iter = threadsList.iterator();
					
					while (iter.hasNext()) {
						try {
							iter.next().join();
						}
						catch (InterruptedException oops) {
							logger.error("Interrupted: ", oops);
							//return 1;
						}
					}
					
					if(logger.isInfoEnabled()) logger.info("<<<< ALL THREADS EXECUTED SUCCESSFULLY >>>>");
					
					return true;
		} catch(Exception e) {
			logger.error("Exception : Error occured while sending doble optin mails ** ",e);
			return false;
		}		
		
	}
	
	
	public static String SENDER_EMAIL;
	public static String SENDER_REPLY_TO_EMAIL;
	/**
	 * 
	 * @param campaign
	 * @param htmlContent
	 * @param textContent
	 * @param campaignReport
	 * @param totalSizeInt
	 * @param eventTrigger
	 * @param fromSource
	 * @return
	 *     This method submits normal campaign in a multi threaded way.
	 *
	 */
	public boolean submitCampaign(Campaigns campaign, String htmlContent, String textContent,
			CampaignReport campaignReport, int totalSizeInt, EventTrigger eventTrigger,String fromSource) {
		
		try {
			if(logger.isDebugEnabled()) logger.debug(">>>>> TOTSize="+totalSizeInt+"  FromSource="+fromSource);
			if(logger.isDebugEnabled()) logger.debug("************* Just Entered SendGrid*************");
		
			/*boolean opensFlag = ((fromSource.startsWith(Constants.SOURCE_MARKETING_PROGRAM))|| (eventTrigger != null 
					&& eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CAMPAIGN_OPEN))?true:false);
			
			boolean clicksFlag = ((fromSource.startsWith(Constants.SOURCE_MARKETING_PROGRAM)) || (eventTrigger != null 
					&& eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CAMPAIGN_CLICK))?true:false);
			*/
			//do i need to give provision for changing from name for program based campaign?
			
			if((eventTrigger != null) && (eventTrigger.getSelectedCampaignFromEmail() != null) && (eventTrigger.getSelectedCampaignFromName() != null)) {

				SENDER_EMAIL =  eventTrigger.getSelectedCampaignFromName() + "<" + eventTrigger.getSelectedCampaignFromEmail() + ">";
				SENDER_REPLY_TO_EMAIL = eventTrigger.getSelectedCampaignReplyEmail();
			}
			else {

				SENDER_EMAIL =  campaign.getFromName() + "<" + campaign.getFromEmail() + ">";
				SENDER_REPLY_TO_EMAIL = campaign.getReplyEmail();
			}
			

			Set<MailingList> mlSet = null;
			MailingList mailingList = null;
			String listIdsStr = Constants.STRING_NILL;
			
			if(fromSource.startsWith(Constants.SOURCE_CAMPAIGN) ) {

				mlSet = campaign.getMailingLists();
			}
			/*else if(fromSource.startsWith(Constants.SOURCE_TRIGGER)
					&&( eventTrigger.getTriggerType().equals(Constants.ET_TYPE_SPECIFIC_DATE)
							|| eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CONTACT_DATE) ) ) {

				mlSet = eventTrigger.getMailingLists();

			}*/
			else if(fromSource.startsWith(Constants.SOURCE_TRIGGER) )  {

				mlSet = eventTrigger.getMailingLists();

			}
			
			
			/*
			 * mlSet should be >0 only for SubmitCampaign/EventTriggers(CDATE/SDATE)...
			 * in other words all cases except EventTrigger's OPENS/CLICKS..hence below condition
			 */
			/*if(!opensFlag && !clicksFlag) { //is it for normal campaignsending?

				if(mlSet == null || mlSet.size() <= 0 ) {

					if(logger.isInfoEnabled()) logger.info("No mailingLists found for the given campaign...exiting");
					return false;
				}



				for (Iterator<MailingList> iterator = mlSet.iterator(); iterator.hasNext();)
				{

					mailingList = iterator.next();
					if(listIdsStr.length() == 0)
						listIdsStr += mailingList.getListId();
					else
						listIdsStr += ","+mailingList.getListId();
				}
			}*/

			/*if(logger.isDebugEnabled()) {
				logger.info(">>>>>>>>> Configured List Ids for the campaign - "+
						campaign.getCampaignName() +":"+listIdsStr);
			}*/
			
				//new event trigger implementation
				if(mlSet == null || mlSet.size() <= 0 ) {

					if(logger.isInfoEnabled()) logger.info("No mailingLists found for the given campaign...exiting");
					return false;
				}



				for (Iterator<MailingList> iterator = mlSet.iterator(); iterator.hasNext();)
				{

					mailingList = iterator.next();
					if(listIdsStr.length() == 0)
						listIdsStr += mailingList.getListId();
					else
						listIdsStr += ","+mailingList.getListId();
				}
			
			
			
			
			String mailSmtpFrom = "v2-[sentId]@";
			String vmta = Constants.STRING_NILL;
			//String vmta = campaign.getUsers().getVmta(); EventTrigger
			if(logger.isInfoEnabled()) logger.info("fromSource "+fromSource+" vmta "+vmta);
			if((fromSource.startsWith(Constants.SOURCE_CAMPAIGN))  || (fromSource.startsWith(Constants.SOURCE_MARKETING_PROGRAM)))
			{  	if(logger.isInfoEnabled()) logger.info("source camp");
				//vmta = campaign.getUsers().getVmta();
				  vmta = campaign.getUsers().getVmta().getVmtaName();
			}
			
			else if(fromSource.startsWith(Constants.SOURCE_TRIGGER))
			{  	if(logger.isInfoEnabled()) logger.info("source trigger");
			try {
				//vmta = eventTrigger.getUsers().getVmta();
				vmta = eventTrigger.getUsers().getVmta().getVmtaName();
			}
			catch(Exception e){
				logger.info("exception while getting user vmta ");
				logger.error("Exception ::::" , e);
			}
			}
			if(logger.isInfoEnabled()) logger.info("fromSource "+fromSource+" vmta "+vmta);
			if(vmta != null) {
				if(vmta.indexOf('.') == -1) {
					if(logger.isInfoEnabled()) logger.info("submitCampaign:vmta.indexOf('.') == -1");
					mailSmtpFrom +=  vmta + ".info";

				}
				else {

					mailSmtpFrom +=  vmta;
					vmta = vmta.substring(0,vmta.indexOf('.'));
					if(logger.isInfoEnabled()) logger.info("submitCampaign:vmta.indexOf('.') != -1 vmta = "+vmta);
				}
			}
			else {
				mailSmtpFrom += PropertyUtil.getPropertyValue("bounceDomain");
			}

			if(logger.isDebugEnabled()) {
				logger.info(">>>>>>> MailSmtpFrom Header :"+mailSmtpFrom);
			}

			/**
			 * last parameter is null if unsegmented campaign.
			 * If campaign is segmented then it should be the query
			 */
			if(logger.isDebugEnabled()) logger.debug(" before callin RecipientProvider constructor..value of listIdsStr = "+listIdsStr);

			// ** 			
			Set<String> totalPhSet = PmtaMailmergeSubmitter.getTotalPhSet();
			/*OrganizationStoresDao organizationStoresDao = null;
			Long orgId = null;*/
			
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
			Long orgId = campaign.getUsers().getUserOrganization().getUserOrgId();//lazy = false on campaign's user
			
			
			if(totalPhSet != null && totalPhSet.size() > 0) {
				
				
				List<OrganizationStores> listOfStores = null;
				
				for (String cfStr : totalPhSet) {
					
					if(OrgStoreMap != null && OrgStoreAddressMap != null) break;//this helps only when these two type of placeholders exists
					
					if(!cfStr.startsWith("GEN_")) continue;//not to consider other place holders
					
					cfStr = cfStr.substring(4);
					
					
					if(listOfStores == null && 
							(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE) || 
									cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) ||
									cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || 
							cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE) || 
							cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) ) {
						
						if(userAddress == null) {//to avoid generate useraddress every-time ;
							//first this has to be done as if no stores exists then user address should be there
							
							Users user = campaignReport.getUser();
							UsersDao usersDao = (UsersDao)context.getBean("usersDao");
							List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
							String userDomainStr = Constants.STRING_NILL;
							Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
							if(domainsList != null) {
								domainSet.addAll(domainsList);
								for (UsersDomains usersDomains : domainSet) {
									
									if(userDomainStr.length()>0) userDomainStr+=",";
									userDomainStr += usersDomains.getDomainName();
									
								}
							}
							String footerAddr[] = PrepareFinalHTML.getOrgAndSenderAddress(user, true, userDomainStr);
							
							userAddress = footerAddr[1];
							
							
						}//if
						
						listOfStores = organizationStoresDao.findByOrganization(orgId);
						if(listOfStores == null || listOfStores.size() == 0) break;
						
						
						
						
					}//if
					
					if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE) ||
							(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE.toLowerCase()) && 
									!cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS) )){
						
						if(OrgStoreMap == null ) { //to get one-time parameter values
							
							OrgStoreMap = new HashMap<String, OrganizationStores>();
							
							
						}//if
						
					
						for (OrganizationStores organizationStores : listOfStores) {
							
							OrgStoreMap.put(organizationStores.getHomeStoreId(), organizationStores);
							
						}//for
						
					}//if
					else if( cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || 
							cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE) || 
							cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) {
						
						if(OrgStoreAddressMap == null ) { //to get one-time parameter values,is this really need????????????
							
							OrgStoreAddressMap = new HashMap<String, String>();
							
							
						}//if
						for (OrganizationStores organizationStores : listOfStores) {
							
							
							String storeAddress = Constants.STRING_NILL;
							String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
							int count = 0;
							for(String str : strAddr){
								count++;
								
								if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
									storeAddress = storeAddress+" | Phone: "+str;
								}
								else if(storeAddress.length()==0 && str.trim().length()>0){
									storeAddress = storeAddress+str;
								}
								else if(storeAddress.length()>0 && str.trim().length()>0){
									storeAddress = storeAddress+", "+str;
								}
							}
							
							OrgStoreAddressMap.put(organizationStores.getHomeStoreId(), storeAddress);
							
						}//for
						
						
					}//else if
					
					
				}//for
				
				
			}//if
			
			
			
			
			
			
			logger.debug("totalPhSet <<<<<<<<< "+ totalPhSet);

		
			
			//htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
			//textContent = textContent.replace("|^", "[").replace("^|", "]");
			
			String orgHtmlContent = htmlContent;
			String campaignSubject = Constants.STRING_NILL;
			String campOrgSubject = campaign.getSubject().replace("|^", "[").replace("^|", "]");
			
			//************* Code to replace symbols and Date of email campaign*****************
			Set<String> symbolSet = getSubjectSymbolAndDateFields(orgHtmlContent+textContent+campOrgSubject);
			if(symbolSet != null && symbolSet.size()>0){
				
				//logger.debug("symbolSet ==========>"+symbolSet);
				for (String symbol : symbolSet) {
						if(symbol.startsWith(Constants.SYMBOL_PH_SYM)) {
							campOrgSubject = campOrgSubject.replace("["+symbol+"]", PropertyUtil.getPropertyValueFromDB(symbol));
						
						}
						else if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
							if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
								Calendar cal = MyCalendar.getNewCalendar();
								campOrgSubject = campOrgSubject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						    	orgHtmlContent = orgHtmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						    	textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
								campOrgSubject = campOrgSubject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						    	orgHtmlContent = orgHtmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						    	textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
								
								try {
									String[] days = symbol.split("_");
									Calendar cal = MyCalendar.getNewCalendar();
									cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
									campOrgSubject = campOrgSubject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									orgHtmlContent = orgHtmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
								} catch (Exception e) {
									logger.debug("exception in parsing date placeholder");
								}
							}
						}
				}
			}
			
			campaignReport.setContent(orgHtmlContent);
			
			//commented for not to save anything related to campaign report here, as everything will be saved in pmta
			
			CampaignReportDao campaignReportDao = (CampaignReportDao)context.getBean("campaignReportDao");
			CampaignReportDaoForDML campaignReportDaoForDML = (CampaignReportDaoForDML)context.getBean("campaignReportDaoForDML");
			
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			
			
			RecipientProvider recipientProvider =
				new RecipientProvider(context, campaignReport, listIdsStr, totalSizeInt, campaign.getCampaignId(), totalPhSet);
			
			
			// Handler for "ExternalEmail"
			if(campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
					campaign.getLabel().trim().startsWith("SENT_")==false) {

				//no changes are reqd in submitExternalEmail for event trigger
				if(eventTrigger != null) {
					return submitExternalEmail(campaign, campaignReport, recipientProvider,eventTrigger) == 0 ? true : false;
				}
				else {
					return submitExternalEmail(campaign, campaignReport, recipientProvider, null) == 0 ? true : false;
				}
			}// if "ExternalEmail"
			
			//********************************END********************************************
			
			
			Set personTagsSet = getSubjectCustomFields(campOrgSubject);
			if(personTagsSet != null && personTagsSet.size() > 0) {
				
				
				if(totalPhSet == null) {
					//totalPhSet = new HashSet<String>();
					totalPhSet = personTagsSet;
				} else {
					totalPhSet.addAll(personTagsSet);
				}	
			}
			
			//logger.debug("Campaign Subject IS: "+ campOrgSubject);
			
			// Create threads to submit campaign
			List<Thread> threadsList = new ArrayList<Thread>();
			try {
				if(logger.isInfoEnabled()) logger.info("<<< STARTING THE THREADS >>>>");
				
				for(int i=0;i < NUMBER_OF_THREADS; i++) {
					
					Thread th = new MultiThreadedSubmission(recipientProvider, campaign, orgHtmlContent, textContent,
							campOrgSubject, campaignReport, totalPhSet, Constants.SOURCE_CAMPAIGN, context, SMTP_AUTH_USER, SMTP_AUTH_PWD );
					th.setName("thread_campaingEmail:" + (i+1));
					threadsList.add(th);
					th.start();
				}
			} catch (Exception e) {
				logger.error("Exception : ERROR OCCURED WHILE CREATING THREADS ...",e);
			}
			
			// wait for all threads to finish
			Iterator<Thread> iter;
			iter = threadsList.iterator();
			
			while (iter.hasNext()) {
				
				try {
					
					iter.next().join();
				}
				catch (InterruptedException oops) {
					
					logger.error("Interrupted: ", oops);
					//return 1;
				}
			}
			if(logger.isInfoEnabled()) logger.info("<<< ALL THREAD EXECUTED SUCCESSFULLY >>>>");
			
			if(logger.isInfoEnabled()) logger.info("** Campaign sending from sendgrid finished .. returning... ");
			return true;
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return false;
		}
				
	}
	
	 private BodyPart htmlBodyPart = null;
	
	/**
	 * External Email Submitter
	 * @param campaign
	 * @param campaignReport
	 * @param recipientProvider
	 * @return
	 * eventTrigger object is added to include EventTrigger code for replyEmail
	 *
	 */
	private byte submitExternalEmail(Campaigns campaign,



			CampaignReport campaignReport, RecipientProvider recipientProvider,EventTrigger eventTrigger) {
		try {
			/*File emlFileNewDir =null;
	        emlFileNewDir = new File("C:\\Users\\Krishna\\workspace\\subscriber\\WebContent\\UserData\\krishna\\ExternalMails\\New");
	        File emlProcessedDir = new File("C:\\Users\\Krishna\\workspace\\subscriber\\WebContent\\UserData\\krishna\\ExternalMails\\processed");
		        File newEmlFiles[] = emlFileNewDir.listFiles();

	        if(newEmlFiles==null || newEmlFiles.length <= 0) {
	        	logger.info("No Mails to send....");
	        	return 1;
	        } //
*/
	        String emailFileName = campaign.getLabel();
	       // String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
	        String serverName = Constants.PROPS_KEY_SERVERNAME;
	        String eeProcessFolder = PropertyUtil.getPropertyValue("externalEmailProcessFolder");


	        File newEmlFile = new File(eeProcessFolder, "Ready" + File.separator +emailFileName);

	        File moveEmlFile = new File(eeProcessFolder, "Processed" + File.separator +emailFileName);

			if(logger.isInfoEnabled()) logger.info("EmlFile :"+newEmlFile.getAbsolutePath());

			if(!newEmlFile.exists()) {
				if(logger.isErrorEnabled()) logger.error("EmailFile is not present----: "+newEmlFile.getAbsolutePath());
				return 2;
			}


			Properties props = System.getProperties();
			Session session = Session.getInstance(props, null);
			Transport trans = session.getTransport("smtp");

			// Assuming that PowerMTA runs on the same machine as this code;
			// otherwise adjust below. Put in user name and password if needed.
			//String host = "174.132.198.218";
			
			String host =  ExternalSMTPSender.SMTP_HOST_NAME;//PropertyUtil.getPropertyValueFromDB("SMTPHost");
			int port =  587;//25; // default SMTP port
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
		
			//replyEmail added for EventTrigger
			String replyEmail = ((eventTrigger != null && eventTrigger.getSelectedCampaignReplyEmail() != null) ? eventTrigger.getSelectedCampaignReplyEmail():campaign.getReplyEmail());
			trans.connect(host, port, username, password);

			FileInputStream fis =  new FileInputStream(newEmlFile);
			SMTPMessage smtpMessage = new SMTPMessage(session, fis );
			fis.close();

			if(! newEmlFile.renameTo(moveEmlFile) ) {
				if(logger.isInfoEnabled()) logger.info("Unable to rename the file: "+newEmlFile.getAbsolutePath());
			} // if

			updateMessageContent(smtpMessage, campaign);

			String htmlRetStr = PrepareFinalHTML.prepareStuff(campaign);
			campaign.setFinalHtmlText(htmlRetStr);
			campaign.setPrepared(true);


			smtpMessage.removeHeader("Delivered-To");
			smtpMessage.removeHeader("CC");
			smtpMessage.removeHeader("BCC");

			smtpMessage.setFrom( new InternetAddress(ExternalSMTPSender.SENDER_EMAIL));
			smtpMessage.setSubject(campaign.getSubject());

			Address intAddress[] = new InternetAddress[1];

			//intAddress[0] =  new InternetAddress(campaign.getReplyEmail());
			intAddress[0] =  new InternetAddress(replyEmail);
			smtpMessage.setReplyTo( intAddress );

			//smtpMessage.setHeader("userId", ""+campaign.getUsers().getUserId());
			//smtpMessage.setHeader("crId", ""+campaignReport.getCrId());
			String messageHeader = "{\"unique_args\": {\"Email\": \""+ "ExternalEmail" +"\", \"userId\": \""+ campaign.getUsers().getUserId() +"\", \"crId\": \""+ campaignReport.getCrId() +"\", \"ServerName\": \""+ serverName +"\" }}";
			smtpMessage.setHeader("X-SMTPAPI",messageHeader);

			smtpMessage.setSentDate(new Date());
			//smtpMessage.addHeader("x-job", "ExternalMail");
           
			// TODO: Check the message header and add the while to thread code wiht sentid in header .
			
			Address to[] = new InternetAddress[1];

			Contacts contact = null;
			while((contact = recipientProvider.getNext()) != null) {

				if(htmlBodyPart!=null) {
					htmlBodyPart.setContent(
							campaign.getFinalHtmlText().replace(
									"[sentId]", ((CampaignSent)contact.getTempObj()).getSentId().longValue()+Constants.STRING_NILL).replace(
											"[email]", contact.getEmailId()), "text/html;");
				}
				
				smtpMessage.setHeader("sentId", ((CampaignSent)contact.getTempObj()).getSentId().longValue()+Constants.STRING_NILL);

				to[0] = new InternetAddress(contact.getEmailId().trim());
				smtpMessage.setRecipients(javax.mail.Message.RecipientType.TO, to);

				smtpMessage.saveChanges();
				trans.sendMessage(smtpMessage, to); 

			} 
			
			trans.close();

			return 0;

		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return 2;
		}

	} // submitExternalEmail
	
	private void updateMessageContent(SMTPMessage smtpMessage, Campaigns campaign) {
		try {
			if(smtpMessage.getContent() instanceof Multipart) {
				dispMultiPart((Multipart)smtpMessage.getContent(), campaign);
				smtpMessage.saveChanges();
			}

		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}

    } // updateMessageContent(smtpMessage)
	
	 private void dispMultiPart(Multipart multipart, Campaigns campaign) throws Exception {

	    	List<BodyPart> removeBodyPartList = new ArrayList<BodyPart>();
			for (int x = 0; x < multipart.getCount(); x++) {

				if(logger.isInfoEnabled()) logger.info("  :BodyPart("+x+"): " + multipart.getBodyPart(x).getClass());

				BodyPart bodyPart = multipart.getBodyPart(x);

				String disposition = bodyPart.getDisposition();
				String contentType = bodyPart.getContentType();

				if(logger.isInfoEnabled()) logger.info("    -----> disposition : "+disposition);
				if(logger.isInfoEnabled()) logger.info("    -----> content type: "+contentType);
				String cidArr[] = null;


				if(contentType!=null && contentType.trim().startsWith("image/")) {
					cidArr = bodyPart.getHeader("Content-ID");
					if(logger.isInfoEnabled()) logger.info("  file name : " + bodyPart.getFileName()+ " and Cid="+cidArr);

					if(cidArr !=null) {
						for (String cid : cidArr) {
							if(logger.isInfoEnabled()) logger.info("Content Id:"+ cid);
						}
					}
				}

				if (cidArr!=null ||
					(disposition != null &&	(disposition.equals(BodyPart.ATTACHMENT) ||
											 disposition.equals(BodyPart.INLINE))) ) {


					if(logger.isInfoEnabled()) logger.info("  Mail have some attachment : ");

	/*				DataHandler handler = bodyPart.getDataHandler();
					logger.info("  file name : " + handler.getName());
	*/
					if(logger.isInfoEnabled()) logger.info("  attachment data : " + contentType);
					if(logger.isInfoEnabled()) logger.info("  Content : " + bodyPart.getContent());

					//String fileName = handler.getName().toLowerCase().trim();

					String fileName = bodyPart.getFileName();

					if(fileName != null) {
						fileName = fileName.trim().replace(" ", "_");
					}

					if(fileName==null && cidArr !=null) { 		//  To handle emails of type "Forward".

							for (String cid : cidArr) {
								if(logger.isInfoEnabled()) logger.info("Content Id:"+ cid);

								if(cid.indexOf('<')!=-1 && cid.indexOf('>')!=-1)
									cid = cid.substring(cid.indexOf('<')+1, cid.indexOf('>'));

								if(cid.indexOf('@')!=-1) cid = cid.substring(0, cid.indexOf('@'));

								fileName = cid.replace('.', '_').trim();
							} // for

							if(contentType.startsWith("image/")) {

								String ext=contentType.substring(contentType.indexOf('/')+1).trim();
								if(ext.indexOf(';')!=-1) ext = ext.substring(0,ext.indexOf(';'));
								if(ext.indexOf(' ')!=-1) ext = ext.substring(0,ext.indexOf(' '));
								if(ext.equalsIgnoreCase("jpeg")) ext = "jpg";
								fileName = fileName+"."+ext;
							}
					}

					if(logger.isInfoEnabled()) logger.info("  file name : " + fileName);

					if(fileName==null) {
						if(logger.isErrorEnabled()) logger.error(" Unable to Get/generate the Image File name for content type =" + contentType);
						continue;
					}


					if(cidArr !=null) {
						for (String cid : cidArr) {
							if(logger.isInfoEnabled()) logger.info("Content Id:"+ cid);

							// if(cid.indexOf('<')!=-1 && cid.indexOf('>')!=-1) cid = cid.substring(cid.indexOf('<')+1, cid.indexOf('>'));
							// cidFileMap.put(cid, fileName);
						} // for

						if(fileName.endsWith(".jpg") || fileName.endsWith(".bmp") || fileName.endsWith(".gif")
								|| fileName.endsWith(".png") || fileName.endsWith(".jpeg") ) {

							removeBodyPartList.add(bodyPart);
						}
					}

					if (bodyPart.getContent() instanceof Multipart) {
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE Attachment =====================");
						dispMultiPart((Multipart)bodyPart.getContent(), campaign);
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE RETURNED  Attachment =====================");
					}
				}
				else {

					if(bodyPart.getContent() instanceof SharedByteArrayInputStream) {

						if(logger.isInfoEnabled()) logger.info("  >>>>> ShBaIs="+bodyPart.getContent());
						SharedByteArrayInputStream tempSb = (SharedByteArrayInputStream)bodyPart.getContent();

						DataInputStream dis = new DataInputStream( new BufferedInputStream(tempSb));
						String tempStr=null;

						while( (tempStr = dis.readLine())!= null ) {
						if(logger.isInfoEnabled()) logger.info("  >>>>> "+tempStr);
						}
					}
					else if (bodyPart.getContent() instanceof Multipart) {
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE =====================");
						dispMultiPart((Multipart)bodyPart.getContent(), campaign);
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE RETURNED =====================");
					}
					else if (bodyPart.getContent() instanceof MimeMessage) {
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE MM =====================");
						MimeMessage mm = (MimeMessage)bodyPart.getContent() ;
						if(logger.isInfoEnabled()) logger.info("  MM Type="+mm.getContent());

						if(mm.getContent() instanceof Multipart) {
							dispMultiPart((Multipart)mm.getContent(), campaign);
						}
						if(logger.isInfoEnabled()) logger.info("  ========================= RECURSIVE RETURNED MM=====================");
					}
					else {
						if(logger.isInfoEnabled()) logger.info("  ShBaIs= NOT "+bodyPart.getContent().getClass() );
						if(logger.isInfoEnabled()) logger.info("  data : " + bodyPart.getContentType());
						if(logger.isInfoEnabled()) logger.info("  -------- "+bodyPart.getContent());

						if(bodyPart.getContentType().contains("text/html;")) {

							if(htmlBodyPart != null) {

								if(logger.isErrorEnabled()) logger.error("======= More than one HTML Content =============");
								if(logger.isErrorEnabled()) logger.error("=====Part1=====\r\n" + htmlBodyPart.getContent() +
										"\r\n =====Part2=====\r\n" + bodyPart.getContent());
							} // if
							else {

								htmlBodyPart = bodyPart;
							}

						}

						else if(bodyPart.getContentType().contains("text/plain;")) {
							// bodyPart.setText(bodyPart.getContent().toString().replace("Friend","Friend KKKKK "));
						}

					}
				} // else

			} // for x

			for (BodyPart tempBodyPart : removeBodyPartList) {

				if(logger.isInfoEnabled()) logger.info("Removing body parts: "+tempBodyPart);
				multipart.removeBodyPart(tempBodyPart);
			} // for

		} // dispMultiPart
		
		
	private Set getSubjectCustomFields(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set subjectPhSet = new HashSet<String>();
		String cfNameListStr = Constants.STRING_NILL;

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					subjectPhSet.add(ph);
				}
				else if(ph.startsWith("CC_")) {
					subjectPhSet.add(ph);
				}
				else if(ph.startsWith("CF_")) {
					subjectPhSet.add(ph);
					cfNameListStr = cfNameListStr + (cfNameListStr.equals(Constants.STRING_NILL) ?  Constants.STRING_NILL : Constants.DELIMETER_COMMA)
											+ "'" + ph.substring(3) + "'";
				} // if(ph.startsWith("CF_"))
				
			} // while
			
			//logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}

		if(logger.isInfoEnabled()) logger.info("CF PH cfNameListStr :" + cfNameListStr + " Set : "+ subjectPhSet);

		return subjectPhSet;
	}
	
	private Set<String> getSubjectSymbolAndDateFields(String content) {
		
		content = content.replace("|^", "[").replace("^|", "]");
		
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> subjectSymbolSet = new HashSet<String>();

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith(Constants.SYMBOL_PH_SYM)) {
					subjectSymbolSet.add(ph);
				}
				else if(ph.startsWith(Constants.DATE_PH_DATE_)){
					subjectSymbolSet.add(ph);
				}
				
			} // while
			
			//logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the symbol place holders ", e);
		}

		if(logger.isInfoEnabled()) logger.info("symbol PH  Set : "+ subjectSymbolSet);

		return subjectSymbolSet;
	}
	
	
	public void submitEmailToMultipleAddress(String messageHeader,String htmlContent, String fromField,String replyTo,
			String subject,String toField, String ccEmailId ) {
				
		logger.info("inside send email to multiple addresses");
				try {
									
				        Authenticator auth = new SMTPAuthenticator();
				        
				        Session mailSession = Session.getInstance(props, auth);
				    
				        // uncomment for debugging infos to stdout
				        //mailSession.setDebug(true);
				        Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
				 
				        MimeMessage message = new MimeMessage(mailSession);
				 
				        Multipart multipart = new MimeMultipart("alternative");
				 
				        BodyPart part2 = new MimeBodyPart();
				        part2.setContent(htmlContent, "text/html");
				 
				        multipart.addBodyPart(part2);
				 
				        message.addHeader("X-SMTPAPI", messageHeader);
					        
					    message.setContent(multipart);
					    
					    message.setFrom(new InternetAddress(fromField));
					  
					    Address intAddress[] = new InternetAddress[1];

						intAddress[0] =  new InternetAddress(replyTo);
						message.setReplyTo( intAddress );
					    message.setSubject(subject);
					    message.addRecipient(Message.RecipientType.TO,  new InternetAddress(Constants.SMTP_RECIEPIENT_EMAIL_ID));

					    // Added for CC Email Id
					    
					    if(ccEmailId != null){
					    	
					    	message.addRecipient(Message.RecipientType.CC,new InternetAddress(ccEmailId));
					    	
					    }
					    
					   				    
					    transport.connect();
				        transport.send(message);
				        
				        logger.info("mail sent for multiple contacts");
				        
				        logger.info("exit send email to multiple addresses");
				} catch(Exception e) {
					logger.error("** Exception : ",e);
				} 
			}
	
	
	
	
		
	//public void submitSimpleEmail(Campaigns campaign,String htmlContent,String textContent,Long crId,Contacts contact,String sourceType) {
	public void submitSimpleEmail(String messageHeader,String htmlContent,String textContent, 
			String fromField,String replyTo, String subject,String toField, String ccEmailId ,String personalizeTo,String sourceType) {
		
		try {
							
		        Authenticator auth = new SMTPAuthenticator();
		        
		        Session mailSession = Session.getInstance(props, auth);
		    
		        // uncomment for debugging infos to stdout
		        //mailSession.setDebug(true);
		        Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
		 
		        MimeMessage message = new MimeMessage(mailSession);
		 
		        Multipart multipart = new MimeMultipart("alternative");
		 
		        BodyPart part1 = new MimeBodyPart();
		        part1.setText(textContent);
		 
		        BodyPart part2 = new MimeBodyPart();
		        part2.setContent(htmlContent, "text/html");
		 
		        multipart.addBodyPart(part1);
		        multipart.addBodyPart(part2);
		 
		        message.addHeader("X-SMTPAPI", messageHeader);
			        
			    message.setContent(multipart);
			    
			    message.setFrom(new InternetAddress(fromField));
			    Address intAddress[] = new InternetAddress[1];

				//intAddress[0] =  new InternetAddress(campaign.getReplyEmail());
			    if(replyTo != null)
			    {
				intAddress[0] =  new InternetAddress(replyTo);
				message.setReplyTo( intAddress );
			    }
			    
			    message.setSubject(subject);
			    message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField, personalizeTo));
			    
			    // Added for CC Email Id
			    
			    if(ccEmailId != null){
			    	
			    	message.addRecipient(Message.RecipientType.CC,new InternetAddress(ccEmailId));
			    	
			    }
			    
			    
		        transport.connect();
		        transport.sendMessage(message,message.getRecipients(Message.RecipientType.TO));
		        transport.close();
		        
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	}
	

	 public class SMTPAuthenticator extends javax.mail.Authenticator {
	      public PasswordAuthentication getPasswordAuthentication() {
	           return new PasswordAuthentication(SMTP_SINGLE_AUTH_USER, SMTP_SINGLE_AUTH_PWD);
	      }
	 }
	
	
}

/**
 * 
 * @author vinay
 *  This thread Enables to submit campaigns synchronously. consists of a private submitSimpleEmail() method. 
 *  Extends lang.Thread class.  
 */

class MultiThreadedSubmission extends Thread {
	
	
	/* private static final Properties props;
	 private static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	 private static final String SMTP_AUTH_USER = "captiway";
	 private static final String SMTP_AUTH_PWD  = "captiway123";*/
	 private  final String serverName = Constants.PROPS_KEY_SERVERNAME;
	 
	/* static {

		 serverName = PropertyUtil.getPropertyValue(Constants.PROPS_KEY_APPLICATION_IP);
	*/		
		/* props = new Properties();
		 props.put("mail.transport.protocol", "smtp");
		 props.put("mail.smtp.host", SMTP_HOST_NAME);
		 props.put("mail.smtp.port", 587);
		 props.put("mail.smtp.auth", "true");*/
	// }
	
	 private static final  Logger logger = LogManager.getLogger(MultiThreadedSubmission.class);
	
	/*private static String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
	private static String webpagelink = PropertyUtil.getPropertyValue("weblinkUrl").replace("|^", "[").replace("^|", "]");
*/	
	private RecipientProvider provider;
	private String campaignType;
	private Campaigns campaign;
	private CustomTemplates customTemplate;
	private String htmlContent;
	private String textContent;
	private Long crId;
	private MailingList mailingList;
	private String supportEmailStr = "support@captiway.com";
	private String campSubject;
	private Set<String> totalPhSet;
	private CampaignReport campaignReport;
	private ApplicationContext context;
	private String smtpUser;
	private String smtpPwd;
	
	RetailProSalesDao retailProSalesDao = null;
	OrganizationStoresDao organizationStoresDao = null;
	ContactsLoyaltyDao contactsLoyaltyDao = null;
	CouponCodesDao couponCodesDao=null;
	CampaignSentDao campaignSentDao;
	CampaignSentDaoForDML campaignSentDaoForDML;
	MailingListDao mailingListDao;
	NotSentToContactsDao notSentToContactsDao;
	
	private static final int NO_OF_MSGS = 1000;
	private volatile int messageCount = NO_OF_MSGS;
	private StringBuffer messageHeader = new StringBuffer(Constants.SENDGRID_HEADER_JSON);
	
	public MultiThreadedSubmission(){
		
	}
	
	public MultiThreadedSubmission(RecipientProvider provider, String campaignType) {
		this.provider = provider;
		this.campaignType = campaignType;
	}
	
	// For Double Optin
	public MultiThreadedSubmission(RecipientProvider provider, MailingList mailingList,String htmlContent, 
			String campaignType, CustomTemplates customTemplate) {

		this.provider = provider;
		this.htmlContent = htmlContent;
		this.campaignType = campaignType;
		this.mailingList = mailingList;
		this.customTemplate = customTemplate;
	}
	
	// For Double Optin
		public MultiThreadedSubmission(RecipientProvider provider, MailingList mailingList,String htmlContent, 
				String campaignType, CustomTemplates customTemplate, ApplicationContext context, Set totalPhSet, String smtpUser, String smtpPwd) {

			this.provider = provider;
			this.htmlContent = htmlContent;
			this.campaignType = campaignType;
			this.mailingList = mailingList;
			this.customTemplate = customTemplate;
			this.context = context;
			this.totalPhSet = totalPhSet;
			this.smtpUser = smtpUser;
			this.smtpPwd = smtpPwd;
		}
	
	
	// For Sending Campaign .
	public MultiThreadedSubmission(RecipientProvider provider, Campaigns campaign, String htmlContent,
			String textContent, String campSubject,CampaignReport campaignReport, Set totalPhSet, String campaignType) { 
		
		this.provider = provider;
		this.campaign = campaign;
		this.htmlContent = htmlContent;
		this.textContent = textContent;
		this.campaignReport = campaignReport;
		this.campSubject = campSubject;
		this.campaignType = campaignType;
		this.totalPhSet = totalPhSet;
	}

	
	public MultiThreadedSubmission(RecipientProvider provider, Campaigns campaign, String htmlContent,
			String textContent, String campSubject,CampaignReport campaignReport, Set totalPhSet, 
			String campaignType, ApplicationContext context, String smtpUser, String smtpPwd) { 
		
		this.provider = provider;
		this.campaign = campaign;
		this.htmlContent = htmlContent;
		this.textContent = textContent;
		this.campaignReport = campaignReport;
		this.campSubject = campSubject;
		this.campaignType = campaignType;
		this.totalPhSet = totalPhSet;
		this.context = context;
		this.smtpUser = smtpUser;
		this.smtpPwd = smtpPwd;
		
	}
	List<CampaignSent> listTobeSaved = new ArrayList<CampaignSent>();
	@Override
	public void run() {
		try {
			
			Long orgId = null;
			String campaignName =null;
			
			if(campaign != null) {
				
				orgId = campaign.getUsers().getUserOrganization().getUserOrgId();
				campaignName  = campaign.getCampaignName();
				
			}else {
				
				
				if(mailingList != null) {
					
					orgId = mailingList.getUsers().getUserOrganization().getUserOrgId();
					if(customTemplate != null) {
						
						campaignName = customTemplate.getTemplateName();
					}
				}
				
				
			}
			couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
			campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
			campaignSentDaoForDML = (CampaignSentDaoForDML)context.getBean("campaignSentDaoForDML");
			retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
			organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
			contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
			mailingListDao = (MailingListDao)context.getBean("mailingListDao");
			notSentToContactsDao = (NotSentToContactsDao)context.getBean("notSentToContactsDao");
			
			
			ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(context, orgId, provider, campaignName);
			
			if(logger.isInfoEnabled()) logger.info("--------- SENDGRID Thread started --------"+ Thread.currentThread().getName());
	    	
	    	Contacts contact = provider.getNext();
	    	
	    	//Iterator<Contacts> iter = provider.getIter();
	    	
	    	
	    	// Check if the contact object exists or not
	    	if(contact == null ) {
	    		if(logger.isInfoEnabled()) logger.info("--------- No Recipients to send, hence Sender Thread exiting -------"+ Thread.currentThread().getName());
	    		return;
	    	}
	    	
	    	
	    	
	  
	    	
	    	String tempHtmlContent = null;
	    	String tempTextContent = null;
	    	String tempCampSubject = null;
	    	
	    	tempHtmlContent = htmlContent;
    		tempTextContent = textContent;
    		tempCampSubject = campSubject;
    		String contactPhValStr = null;
    		
	    	// Check if the campaignType is normal campaign ...	    	
	    	if(campaignType.equalsIgnoreCase(Constants.SOURCE_CAMPAIGN)) {
	    		
	    		if(htmlContent == null || campSubject == null || totalPhSet == null) {
	    			if(logger.isErrorEnabled()) logger.error("--------- REQUIRED PARAMETER MISSING , HENCE SENDER THREAD EXITING -------"+ Thread.currentThread().getName());
	    			if(logger.isErrorEnabled()) logger.error(" htmlContent : " + htmlContent + " campSubject : " + campSubject +  "totalPhSet : " + totalPhSet);
	    			return;
	    			
	    		} 
	    		
	    		//logger.debug(" ********** Starting to send Campaign : "+ campaign.getCampaignName());
	    		
	    		
	    		int conIncrementFlag = 0;//helps to prepare header for ech 1000
	    		
	    		//params required in preparing msg header
	    		JSONObject SendGridHeader = null ;
	    		JSONObject uniqueArgs = null;
	    		JSONArray  toEmailArr = null;
	    		JSONObject toEmail = null;
	    		JSONObject substitutionValues =  null;
	    		
	    		List<ContactPHValue> contactPHValues = null;
	    		List<NotSentToContacts> notSentToContacts = null;
	    		
	    		//replace all the one time placeholders(why can't we take SB here?)
	    		String encUserId = EncryptDecryptUrlParameters.encrypt(campaign.getUsers().getUserId()+Constants.STRING_NILL);
	    		tempHtmlContent = tempHtmlContent.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_USERID, encUserId)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail());
					    		
	    		
	    		tempTextContent = tempTextContent
								.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail())
								.replace(Constants.PLACEHOLDER_USERID, encUserId);
					    		
	    		
	    		
			    		while(contact != null) { //unless provider has no contacts
			    			
			    			CampaignSent contactSentObj = (CampaignSent)contact.getTempObj();
			    			
			    			
			    			conIncrementFlag ++ ;
			    			long sentId = contactSentObj.getSentId().longValue();
			    			ContactPHValue contactPHValue = new ContactPHValue(sentId+Constants.STRING_NILL,
			    											contact.getContactId().longValue()+Constants.STRING_NILL,
			    											contact.getEmailId(), contact.getEmailId());
			    			
			    			
			    			
			    			NotSentToContacts notSentToContact = new NotSentToContacts(campaignReport.getCrId(), sentId);
			    			
			    			
			    			if(SendGridHeader == null) {//first time and for each 1000 contacts this will be done
			    				
			    				contactPHValues = new ArrayList<ContactPHValue>();
			    				
			    				notSentToContacts = new ArrayList<NotSentToContacts>();
			    				//logger.info("creating new "+contact.getContactId().longValue());
			    				SendGridHeader = new JSONObject();
			    				
			    				
			    				
			    				//for back tracking from sendgrid(notifications)
			    				uniqueArgs = new JSONObject();
			    				uniqueArgs.put("Email", Constants.SOURCE_CAMPAIGN);
			    				uniqueArgs.put("crId", campaignReport.getCrId()+Constants.STRING_NILL);
			    				uniqueArgs.put("userId", campaign.getUsers().getUserId().longValue()+Constants.STRING_NILL);
			    				uniqueArgs.put("ServerName", serverName);
			    				
			    				
			    				
			    				SendGridHeader.put("unique_args", uniqueArgs);
			    				
			    				toEmail = new JSONObject(); //TODO toEmail.put("to", toEmailArr);
			    				toEmailArr = new JSONArray();
			    				
			    				//replace system phvals
			    				substitutionValues = new JSONObject();
			    				
			    				substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_SENTID+Constants.RIGHT_SQUARE_BRACKET, new JSONArray());
			    				substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_CID+Constants.RIGHT_SQUARE_BRACKET, new JSONArray());
			    				substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL+Constants.RIGHT_SQUARE_BRACKET, new JSONArray());
			    				substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL_ID+Constants.RIGHT_SQUARE_BRACKET, new JSONArray());
			    				
			    				//create arrays for placeholders
			    				if(totalPhSet != null && totalPhSet.size() > 0) {
			    	    			for (String cfStr : totalPhSet) {
			    	    				
			    	    				substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, new JSONArray());
			    	    				
			    	    				if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_UNSUBSCRIBE_LINK)) {
			    	    					
			    	    					String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
			    	    					String value = "<a href="+unsubURL+" target=\"_blank\">unsubscribe</a>";
			    	    					
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    				}
			    	    				else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH__UPDATE_PREFERENCE_LINK)) {
			    	    					
			    	    					String updateSubscriptionLink = PropertyUtil.getPropertyValue("updateSubscriptionLink").replace("|^", "[").replace("^|", "]");
			    	    					
			    	    					//String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
			    	    					String value = "<a href="+updateSubscriptionLink+" target=\"_blank\">Subscriber Preference Link</a>";
			    	    					
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    	    				}
			    	    				
			    	    				else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_WEBPAGE_VERSION_LINK)) {
			    	    					
			    	    					String webpagelink = PropertyUtil.getPropertyValue("weblinkUrl").replace("|^", "[").replace("^|", "]");
			    	    					
			    	    					String value = "<a style=\"color: inherit; text-decoration: underline; \"  href="+webpagelink+" target=\"_blank\">View in web-browser</a>";
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    	    				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_TWEET_URL)) {
			    	    					
			    	    					String twitterlink = PropertyUtil.getPropertyValue("shareTweetLinkUrl").replace("|^", "[").replace("^|", "]");
			    	    					
			    	    					String value = "<a style=\"color: blue; text-decoration: underline; \"  href="+twitterlink+" target=\"_blank\">Share on Twitter</a>";
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    	    				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_FACEBOOK_URL)) {
			    	    					
			    	    					String fblink = PropertyUtil.getPropertyValue("shareFBLinkUrl").replace("|^", "[").replace("^|", "]");
			    	    					//String value = "<a style=\"color: blue;\"  href="+webpagelink+">Share on <img src=\"http://localhost:8080/subscriber/images/closetree.jpg\"></a> ";
			    	    					String value = "<a style=\"color: blue; text-decoration: underline; \"  href="+fblink+" target=\"_blank\">Share on Facebook</a>";
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    	    				}else if(cfStr.contains(PlaceHolders.CAMPAIGN_PH_FORWRADFRIEND_LINK)) {
			    	    					
			    	    					String farwardFriendLink = PropertyUtil.getPropertyValue("forwardToFriendUrl").replace("|^", "[").replace("^|", "]");
			    	    					
			    	    					 String value = "<a href="+farwardFriendLink+" target=\"_blank\">Forward to Friend</a>";
			    	    					tempHtmlContent = tempHtmlContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
			    	    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    	    				}
			    	    				
			    	    				
			    	    			}//for
			    	    			
			    	    				//if unsub/weblink tags r there then again needed to be replaced
			    	    			//need to know is it needded in the subject or not?
			    	    			tempHtmlContent = tempHtmlContent.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
									.replace(Constants.PLACEHOLDER_USERID, encUserId)
									.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail());
						    		
		    		
			    	    			tempTextContent = tempTextContent
									.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
									.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail())
									.replace(Constants.PLACEHOLDER_USERID, encUserId);
						    	
			    				
			    				
			    				}//if
			    				
			    			}//if (SendGridHeader == null)
						
			    			
			    			
			    			contactPHValues.add(contactPHValue);
			    			notSentToContacts.add(notSentToContact);
				    		
			    			
			    			
				    		if(totalPhSet != null && totalPhSet.size() >0) {
				    			
				    			//to get the substitution values
				    			//perform all the things here itself as bulk sending from here only
				    			
				    			
				    			
				    			List<Object> retList = replacePlaceHolders.getSubStitutions( substitutionValues, contact, totalPhSet, Constants.COUP_GENT_CAMPAIGN_TYPE_EMAIL,
				    					contact.getEmailId(), campaign.getUsers(), ((CampaignSent)contact.getTempObj()).getSentId(), provider, contactPHValue);
				    			
				    			if(retList != null) {
				    				
					    			substitutionValues = (JSONObject)retList.get(0);
						    		contactPhValStr = retList.get(1) == null ? null : (String)retList.get(1);
				    			}
					    		
					    		
					    	}//if
					    	
					    	
					    	if(contactPhValStr != null) {
					    		
						    	contactSentObj.setContactPhValStr(contactPhValStr);
						    	//logger.info("sent temp obj ================>"+contactSentObj.getContactPhValStr());
						    	listTobeSaved.add(contactSentObj);
					    	}
					    	
					    	//for replacing sent ids
					    	JSONArray sentIDsArr = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_SENTID+Constants.RIGHT_SQUARE_BRACKET);
					    	String encSendIds = EncryptDecryptUrlParameters.encrypt(contactSentObj.getSentId().longValue()+Constants.STRING_NILL);
					    	sentIDsArr.add(encSendIds);
					    	substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_SENTID+Constants.RIGHT_SQUARE_BRACKET, sentIDsArr);
					    	
					    	//for replacing cid ids
					    	JSONArray cIDsArr = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_CID+Constants.RIGHT_SQUARE_BRACKET);
					    	String encCIds = EncryptDecryptUrlParameters.encrypt(contact.getContactId().longValue()+Constants.STRING_NILL);
					    	cIDsArr.add(encCIds);
					    	substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_CID+Constants.RIGHT_SQUARE_BRACKET, cIDsArr);
							
					    	
					    	//for replacing emails
					    	JSONArray emailsArr = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL+Constants.RIGHT_SQUARE_BRACKET);
					    	emailsArr.add(contact.getEmailId());
					    	substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL+Constants.RIGHT_SQUARE_BRACKET, emailsArr);
					    	
					    	//for replacing email ids
					    	JSONArray emailIDsArr = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL_ID+Constants.RIGHT_SQUARE_BRACKET);
					    	emailIDsArr.add(contact.getEmailId());
					    	substitutionValues.put(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL_ID+Constants.RIGHT_SQUARE_BRACKET, emailIDsArr);
					    	
					    	
							
							
							//adding 'To' field personalization
								String prsnlizeToFld = 	Constants.STRING_NILL;
								if(campaign.getToName() != null && campaign.getToName().trim().length() > 1)	 {
									
									if(campaign.getToName().equals("firstName")){
										
										prsnlizeToFld =  contact.getFirstName()==null?Constants.STRING_NILL:contact.getFirstName();
										
									}else if(campaign.getToName().equals("lastName")){
										
										prsnlizeToFld =  contact.getLastName()==null?Constants.STRING_NILL:contact.getLastName();
									}else if(campaign.getToName().equals("fullName")) {
										
										prsnlizeToFld = contact.getFirstName()==null?Constants.STRING_NILL:contact.getFirstName();
										prsnlizeToFld += contact.getLastName()==null?Constants.STRING_NILL:" "+contact.getLastName();
										
									}
									
								}
								
							String toEmailId = !prsnlizeToFld.isEmpty() ?  prsnlizeToFld+"<"+contact.getEmailId()+">" : contact.getEmailId();
							toEmailArr.add(toEmailId);
				    		contactPHValue.setToEmail(toEmailId);
							
							
							if(conIncrementFlag >= 1000) {
								
								//prepare JSON structure with 'to' email ids and placeholder values
								SendGridHeader.put("to", toEmailArr);
								SendGridHeader.put("sub", substitutionValues);
								//logger.debug("json array ::"+SendGridHeader.toString());
								
								
								/*JSONObject retVal = validateSMTPHeader(substitutionValues, contactPHValues, totalPhSet, toEmailArr);
								
								if(retVal == null) { //values are wrong need to persists all these and cancel sending
									
									if(notSentToContacts != null && notSentToContacts.size() > 0) {
										
										notSentToContactsDao.saveByCollection(notSentToContacts);
										
									}//if
									
								}//if
*/								
								JSONObject retVal = substitutionValues;
								
								//submit to SENDGRID
								if(retVal != null) {
									
									submitSimpleEmail(SendGridHeader.toString(),tempHtmlContent, tempTextContent, 
											ExternalSMTPSender.SENDER_EMAIL,ExternalSMTPSender.SENDER_REPLY_TO_EMAIL,tempCampSubject, Constants.SOURCE_CAMPAIGN);
								}
								
								//to prepare JSON for every 1000 contacts
								SendGridHeader.clear();
								SendGridHeader = null;
								conIncrementFlag = 0;
								
								//restore the original content
								tempHtmlContent = htmlContent;
					    		tempTextContent = textContent;
					    		tempCampSubject = campSubject;
					    		
	
					    		//replace one time place holders
					    		tempHtmlContent = tempHtmlContent.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_USERID, encUserId)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail());
					    		
					    		
					    		tempTextContent = tempTextContent
								.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail())
								.replace(Constants.PLACEHOLDER_USERID, encUserId);
					    		
					    		
								
								
							}//if
							
							
							
							contact = provider.getNext();//get next contact
							
							
							
							if(listTobeSaved.size() > 1000) {//save  sent objects
								//campaignSentDao.saveByCollection(listTobeSaved);
								campaignSentDaoForDML.saveByCollection(listTobeSaved);
								listTobeSaved.clear();
							}
							
							
							
						} // while
			    		
		    			if( listTobeSaved.size() > 0) {
		    				//campaignSentDao.saveByCollection(listTobeSaved);
		    				campaignSentDaoForDML.saveByCollection(listTobeSaved);
		    				listTobeSaved.clear();
		    			}
			    		

		    			if(conIncrementFlag > 0) {
			    				//avoid code duplication
							SendGridHeader.put("to", toEmailArr);
							SendGridHeader.put("sub", substitutionValues);
							//logger.debug("json array ::"+SendGridHeader.toString());
							
							/*JSONObject retVal = validateSMTPHeader(substitutionValues, contactPHValues, totalPhSet, toEmailArr);
							
							if(retVal == null) { //values are wrong need to persists all these and cancel sending
								
								if(notSentToContacts != null && notSentToContacts.size() > 0) {
									
									notSentToContactsDao.saveByCollection(notSentToContacts);
									
								}//if
*/								
								/*SendGridHeader.clear();
								SendGridHeader = null;
								conIncrementFlag = 0;
								
								
								
								//restore the original content
								tempHtmlContent = htmlContent;
					    		tempTextContent = textContent;
					    		tempCampSubject = campSubject;
					    		
	
					    		//replace one time place holders
					    		tempHtmlContent = tempHtmlContent.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_USERID, campaign.getUsers().getUserId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail());
					    		
					    		
					    		tempTextContent = tempTextContent
								.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
								.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail())
								.replace(Constants.PLACEHOLDER_USERID, campaign.getUsers().getUserId()+Constants.STRING_NILL);
					    		
								
								 return;*/
								
							//}///if
							
							//logger.info("got the header as ::"+SendGridHeader.toString());
							JSONObject retVal = substitutionValues;
							if(retVal != null) { 
								
								submitSimpleEmail(SendGridHeader.toString(),tempHtmlContent, tempTextContent, 
										ExternalSMTPSender.SENDER_EMAIL,ExternalSMTPSender.SENDER_REPLY_TO_EMAIL, tempCampSubject, Constants.SOURCE_CAMPAIGN);
							}//if
							
							SendGridHeader.clear();
							SendGridHeader = null;
							conIncrementFlag = 0;
							
							//no need here
							//restore the original content
							tempHtmlContent = htmlContent;
				    		tempTextContent = textContent;
				    		tempCampSubject = campSubject;
				    		

				    		//replace one time place holders
				    		tempHtmlContent = tempHtmlContent.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
							.replace(Constants.PLACEHOLDER_USERID, campaign.getUsers().getUserId()+Constants.STRING_NILL)
							.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail());
				    		
				    		
				    		tempTextContent = tempTextContent
							.replace(Constants.PLACEHOLDER_CRID, campaignReport.getCrId()+Constants.STRING_NILL)
							.replace(Constants.PLACEHOLDER_SENDEREMAIL, campaign.getFromEmail())
							.replace(Constants.PLACEHOLDER_USERID, campaign.getUsers().getUserId()+Constants.STRING_NILL);
				    		
							
						}
			    		//logger.debug(" ********** Complete to send Campaign : "+ campaign.getCampaignName()+" json arry ::"+SendGridHeader.toString());
			    		
	    		
	    	} else if(campaignType.equalsIgnoreCase(Constants.SOURCE_TESTEMAIL)) {
	    		
	    		
	    		
	    	} else if (campaignType.equalsIgnoreCase(Constants.SOURCE_DOUBLEOPTIN)) {
	    			
			    		if(tempHtmlContent == null) {
			    			
			    			
			    			if(logger.isErrorEnabled()) logger.error("*** Exception : HTML CONTENT IS NULL... RETURNING. ");
			    			return;
			    		}
	    		
			    		Users mlUser = mailingList.getUsers();
			    		UserOrganization userOrg = mlUser.getUserOrganization();
			    		String senderEmail = Constants.STRING_NILL;
			    		String orgName = Constants.STRING_NILL;
						if(userOrg != null) {
							
							orgName = userOrg.getOrganizationName();
							
						}
			    		
			    		while(contact != null) {
			    			
							try {
								
								//logger.debug(" EmailID="+contact.getEmailId());
								
								tempHtmlContent = htmlContent;
								
								//adding 'To' field personalization
								String prsnlizeToFld = 	Constants.STRING_NILL;
								String subject = "Confirmation Request";
								if(customTemplate != null) {
									if(customTemplate.isPersonalizeTo() == true && customTemplate.getToName() != null && customTemplate.getToName().trim().length() > 1)	 {
										
										if(customTemplate.getToName().equals("firstName")){
											
											prsnlizeToFld =  contact.getFirstName()==null?Constants.STRING_NILL:contact.getFirstName();
											
										}else if(customTemplate.getToName().equals("lastName")){
											
											prsnlizeToFld =  contact.getLastName()==null?Constants.STRING_NILL:contact.getLastName();
										}else if(customTemplate.getToName().equals("fullName")) {
											
											prsnlizeToFld = contact.getFirstName()==null?Constants.STRING_NILL:contact.getFirstName();
											prsnlizeToFld += contact.getLastName()==null?Constants.STRING_NILL:" "+contact.getLastName();
											
										}
										
									}
									//*******************
									subject = (customTemplate.getSubject() == null || customTemplate.getSubject().trim().equals(Constants.STRING_NILL)) ? subject : customTemplate.getSubject();
								}
								
								subject = subject.replace("|^", "[").replace("^|", "]");
								if(totalPhSet != null && totalPhSet.size() >0) {
						    		//we will get error in coupon reports coz the sent id will not be thr for this doubl optin contact
						    		String [] contentsStrArr = replacePlaceHolders.getContactPhValue(contact, tempHtmlContent,
						    				tempHtmlContent, subject, totalPhSet, Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL,
						    				contact.getEmailId(), mailingList.getUsers(),  contact.getContactId(),null);
						    		
						    		
						    		
						    		tempHtmlContent = contentsStrArr[0];
						    		//tempTextContent = contentsStrArr[1];
						    		subject = contentsStrArr[2];
						    		
						    	}
								
								
								
								String messageHeader = "{\"unique_args\": {\"Email\": \""+ "DoubleOptin" +"\", \"userId\": \""+ mailingList.getUsers().getUserId() +"\", \"ListId\": \""+ mailingList.getListId() +"\", \"ContactId\" : \""+ contact.getContactId() +"\" , \"ServerName\": \""+ serverName +"\"  }}";
		
								tempHtmlContent = tempHtmlContent.replace("[greetings]", contact.getFirstName()==null?"Dear sir/madam, ":"Hi "+contact.getFirstName()+",")
								.replace("[cId]", contact.getContactId()+Constants.STRING_NILL).replace("[email]", contact.getEmailId());
								
								// tempHtmlContent and text content is same ...
								submitSimpleEmail(messageHeader,tempHtmlContent,tempHtmlContent, ExternalSMTPSender.SENDER_EMAIL, ExternalSMTPSender.SENDER_REPLY_TO_EMAIL, subject  ,contact.getEmailId(), prsnlizeToFld,Constants.SOURCE_DOUBLEOPTIN);
								
							} catch (Exception e) {
								
								if(logger.isErrorEnabled()) logger.error("Error: Exception while sending Double optin email to "+ contact.getEmailId(),e);
							}
							
							// Get the next Contact
							contact = provider.getNext();
							
						} // while
	    		
	    	}
	    	
		} catch(Exception e) {
			
			logger.error("Exception : Error occured while sending the email. ",e);
		} finally {
		
			if(logger.isInfoEnabled()) logger.info("<<--------- SENDGRID "+ Thread.currentThread().getName() +" Exiting -------->>");
			provider.flushCouponCodesToDB(true);
			try {
				if(transport!=null) transport.close();
			} catch (Exception e2) {
				logger.error("Exception ::::", e2);
			}
		}		
	}
	
	
/**
 * Method to validate the SMTP header with substitution tags accross with already </BR>
 * prepared list of ContactPhValues.
 * @param substitutionValues is the JSONObject consists substitution tags
 * @param contactPHValues is the List<ContactPHValue> .
 * @return
 */
	private JSONObject validateSMTPHeader(JSONObject substitutionValues, 
			List<ContactPHValue> contactPHValues, Set<String> totalPhSet, JSONArray toEmailArray) {
		
		
		//logger.info("----1------");
		Class strArg[] = new Class[] { String.class };
			int contactIndex = 0; 
			for (Object object : toEmailArray) {
				
				String emailId = object.toString();
				try {
					//logger.info("----"+contactIndex+"------");
					ContactPHValue contactPHValue = contactPHValues.get(contactIndex);
					if(contactPHValue == null) {
						
						
						logger.info("found object as null");
						contactIndex ++;
						continue;
						
					}
					
					
					if(emailId.equalsIgnoreCase( contactPHValue.getToEmail() ) ) {
						boolean isValidate = true;
						int numberOfValuesWrong = 0;
						StringBuffer wrongvaluesstr = new StringBuffer( Constants.STRING_NILL);//TODO need to delete
						//validate the header
						JSONArray systemPHJSONArray = new JSONArray();
						systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_SENTID+Constants.RIGHT_SQUARE_BRACKET);

						//logger.info("----"+systemPHJSONArray.get(contactIndex).toString()+"-1-----"+contactPHValue.getSentId());
						
						if(!systemPHJSONArray.get(contactIndex).toString().equals(contactPHValue.getSentId())) {
							
							isValidate = false;
							numberOfValuesWrong ++;
							wrongvaluesstr.append("sentId "); 
							
						}//if
						
						
						
						systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_CID+Constants.RIGHT_SQUARE_BRACKET);
						
						//logger.info("----"+systemPHJSONArray.get(contactIndex).toString()+"--2----"+contactPHValue.getCid());
						
						if(!systemPHJSONArray.get(contactIndex).toString().equals(contactPHValue.getCid())) {
							
							isValidate = false;
							numberOfValuesWrong ++;
							wrongvaluesstr.append("cid "); 
							
						}//if
						
						
						systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL+Constants.RIGHT_SQUARE_BRACKET);
						
						//logger.info("----"+systemPHJSONArray.get(contactIndex).toString()+"---3---"+contactPHValue.getSystemPh_email());
						
						if(!systemPHJSONArray.get(contactIndex).toString().equals(contactPHValue.getSystemPh_email())) {
							
							isValidate = false;
							numberOfValuesWrong ++;
							wrongvaluesstr.append("email "); 
							
							
						}//if
						systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+Constants.QS_EMAIL_ID+Constants.RIGHT_SQUARE_BRACKET);
						//logger.info("----"+systemPHJSONArray.get(contactIndex).toString()+"---4---"+contactPHValue.getEmailId());
						
						if(!systemPHJSONArray.get(contactIndex).toString().equals(contactPHValue.getEmailId())) {
							
							isValidate = false;
							numberOfValuesWrong ++;
							wrongvaluesstr.append("emailid "); 
							
						}//if
						
						if(totalPhSet != null && totalPhSet.size() > 0) {
							
							String couponValueStr = Constants.STRING_NILL;
							
							for (String phStr : totalPhSet) {
								
								if(phStr.contains(PlaceHolders.CAMPAIGN_PH_UNSUBSCRIBE_LINK) ||
										phStr.contains(PlaceHolders.CAMPAIGN_PH_WEBPAGE_VERSION_LINK) ||
										phStr.contains(PlaceHolders.CAMPAIGN_PH_TWEET_URL) ||
										phStr.contains(PlaceHolders.CAMPAIGN_PH_FACEBOOK_URL) ||
										phStr.contains(PlaceHolders.CAMPAIGN_PH_FORWRADFRIEND_LINK)||
									phStr.contains(PlaceHolders.CAMPAIGN_PH__UPDATE_PREFERENCE_LINK)) continue;
								
								String getMethosPhStr = phStr.substring(phStr.indexOf("_", 0)+1);
								
								if(getMethosPhStr.contains("/"))
									getMethosPhStr = getMethosPhStr.substring(0, getMethosPhStr.indexOf('/'));
								
								if(phStr.startsWith("CC_")) {
									
									systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+phStr+Constants.RIGHT_SQUARE_BRACKET);
									
									if(!couponValueStr.isEmpty()) couponValueStr += Constants.DELIMETER_DOUBLECOLON;
									
									couponValueStr += systemPHJSONArray.get(contactIndex).toString();
									
								}
								else {
									//logger.info("----"+phStr+"------");
									systemPHJSONArray = (JSONArray)substitutionValues.get(Constants.LEFT_SQUARE_BRACKET+phStr+Constants.RIGHT_SQUARE_BRACKET);
									
									
									
									 String getterName = "get" + ContactPhValuesEnum.getEnumByPlaceHOlder(getMethosPhStr).getGetterMethod();//getter method will not be get properly naming convention may miss
									 try {
									 @SuppressWarnings("unchecked")
									 Method method = ContactPHValue.class.getMethod(getterName);
									 if(method != null) {
									 
										 Object valueObject = method.invoke(contactPHValue, (Object[]) null);
										
										 String  value  = valueObject != null ? valueObject.toString() : "";
										// logger.info("----"+systemPHJSONArray.get(contactIndex).toString()+"------"+value);
										 
										 if(!systemPHJSONArray.get(contactIndex).toString().equals(value)) {
												
												isValidate = false;
												numberOfValuesWrong ++;
												wrongvaluesstr.append(phStr +" "); 
												
												
										}//if
									 
									 }
									 
									 
									 } catch (Exception e) {
										 
										// logger.error("----reflection------",e);
										 
										 
										 
									 }//catch
									
									
								}//else
								
								
								
								
							}//for
							
							if(!couponValueStr.isEmpty()) {
								
								if(contactPHValue.getCouponPHStr() != null && !contactPHValue.getCouponPHStr().equals(couponValueStr) ) {
									
									isValidate = false;
									numberOfValuesWrong ++;
									wrongvaluesstr.append(couponValueStr +"=== "+contactPHValue.getCouponPHStr()); 
									
								}//if
								
							}//if
							
						}//if totalphset
						
						if(!isValidate || numberOfValuesWrong > 0 ) {
							
							logger.info("got some of the value as wrong...."+wrongvaluesstr);
							return null;//so that no nee to send all these 1000 contacts
							
						}
						
					}else{
						//need to provide atleast empty values in the substitution tags header
						/*contactIndex ++;
						continue;*/
						return null;
					}
					contactIndex ++;
					
					
				} catch (IndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					logger.error("no object found in the given index");
					/*contactIndex ++;
					continue;*/
					return null;
				}
				
				
				
				
				
			}
		
			
		
		return substitutionValues;
		
		
		
		
		
	}//validateSMTPHeader
	
	
	
	/*private String getLastPurchaseStoreAddr(Contacts contact) {
		//need to get the contact last puchased store address
		
		
		String storeAddress = Constants.STRING_NILL;
		if(contact.getExternalId() != null) {
			
			String storeNum = retailProSalesDao.findLastpurchasedStore(contact.getExternalId(),contact.getUsers().getUserId());
			if(storeNum != null) {
				OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(campaign.getUsers().getUserOrganization().getUserOrgId(), storeNum);
								
				if(organizationStores == null) {
					storeAddress=Constants.STRING_NILL;
					
				}else {
					storeAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
				}
			}
		}
		return storeAddress;
	}//getLastPurchaseStoreAddr();
	*/
	/*private String getContactLastPurchasedDate(Contacts contact) {
		
		String date = Constants.STRING_NILL;
		
		
		if(contact.getExternalId() != null) {
			
			date = MyCalendar.calendarToString( 
					retailProSalesDao.findLastpurchasedDate(contact.getExternalId(),contact.getUsers().getUserId()), MyCalendar.FORMAT_DATEONLY_GENERAL);
			
		}
		
		return date;
	}//getContactLastPurchasedDate();
	
	*/
	private Authenticator auth = null;
	private Session mailSession = null;
	private Transport transport = null;
	
	private void submitSimpleEmail(String messageHeader,String inHtmlContent,String inTextContent, String fromField, String replyTo,  String subject,String toField, String prsnlizeToFld,String sourceType) {
		
		try { 
				messageCount++;
				
				if(messageCount > NO_OF_MSGS) {
					messageCount=1;
					
					if(transport!=null) transport.close();
					
					auth = new SMTPAuthenticator(this.smtpUser,this.smtpPwd);
					mailSession = Session.getInstance(ExternalSMTPSender.props, auth);
			
					//logger.debug("in html content is=====>"+inHtmlContent);
			        //Session mailSession = Session.getDefaultInstance(props, auth);
			    
			        // uncomment for debugging infos to stdout
			       // mailSession.setDebug(true);
			        transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
			        transport.connect();
				} // if
		 
		        MimeMessage message = new MimeMessage(mailSession);
		 
		        Multipart multipart = new MimeMultipart("alternative");
		 
		        BodyPart part1 = new MimeBodyPart();
		        part1.setText(inTextContent);
		 
		        BodyPart part2 = new MimeBodyPart();
		        part2.setContent(inHtmlContent, "text/html");
		 
		        multipart.addBodyPart(part1);
		        multipart.addBodyPart(part2);
		        
		        message.addHeader("X-SMTPAPI", messageHeader);
			        
		        //logger.info(")))))))>>> "+messageHeader);
			    message.setContent(multipart);
			    
			    message.setFrom(new InternetAddress(fromField));
			    
			    Address intAddress[] = new InternetAddress[1];

				//intAddress[0] =  new InternetAddress(campaign.getReplyEmail());
				intAddress[0] =  new InternetAddress(replyTo);
				message.setReplyTo( intAddress );

			    message.setSubject(subject);
			    message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField, prsnlizeToFld));
		        
		        transport.sendMessage(message,
		        message.getRecipients(Message.RecipientType.TO));
		        
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	}//submitSimpleEmail
	
	//submitSimpleEmail(SendGridHeader.toString(),tempHtmlContent, tempTextContent,ExternalSMTPSender.SENDER_EMAIL,tempCampSubject, Constants.SOURCE_CAMPAIGN);
private void submitSimpleEmail(String messageHeader,String inHtmlContent,String inTextContent, String fromField, String replyTo,  String subject, String sourceType) {
		
		try { 
				messageCount++;
				
				if(messageCount > NO_OF_MSGS) {
					messageCount=1;
					
					if(transport!=null) transport.close();
					
					auth = new SMTPAuthenticator(smtpUser, smtpPwd);
					mailSession = Session.getInstance(ExternalSMTPSender.props, auth);
			
					//logger.debug("in html content is=====>"+inHtmlContent);
			        //Session mailSession = Session.getDefaultInstance(props, auth);
			    
			        // uncomment for debugging infos to stdout
			       // mailSession.setDebug(true);
			        transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
			        transport.connect();
				} // if
		 
		        MimeMessage message = new MimeMessage(mailSession);
		 
		        Multipart multipart = new MimeMultipart("alternative");
		 
		        BodyPart part1 = new MimeBodyPart();
		        part1.setText(inTextContent);
		 
		        BodyPart part2 = new MimeBodyPart();
		        part2.setContent(inHtmlContent, "text/html");
		 
		        multipart.addBodyPart(part1);
		        multipart.addBodyPart(part2);
		        
		        
		        message.addHeader("X-SMTPAPI", messageHeader);
			        
		        //logger.info(")))))))>>> "+messageHeader);
			    message.setContent(multipart);
			    
			    message.setFrom(new InternetAddress(fromField));
			    
			    Address intAddress[] = new InternetAddress[1];

				//intAddress[0] =  new InternetAddress(campaign.getReplyEmail());
				intAddress[0] =  new InternetAddress(replyTo);
				message.setReplyTo( intAddress );
			    message.setSubject(subject);
			    message.addRecipient(Message.RecipientType.TO,  new InternetAddress(Constants.SMTP_RECIEPIENT_EMAIL_ID));
			    
		        
		        /*transport.sendMessage(message,
		        message.getRecipients(Message.RecipientType.TO));*/
			    
				//transport.send(message,  message.getRecipients(Message.RecipientType.TO));
				transport.send(message);
		        
		} catch(Exception e) {
			logger.debug(e.getMessage());
		} 
	}
	
	
	 public class SMTPAuthenticator extends javax.mail.Authenticator {
		 private String userName;
		 private String pwd;
		 public SMTPAuthenticator() {
			 
			 
		 }
		 
		 public SMTPAuthenticator(String userName, String pwd) {
			 this.userName = userName;
			 this.pwd = pwd;
			 
		 }
	      public PasswordAuthentication getPasswordAuthentication() {
	           return new PasswordAuthentication(this.userName, this.pwd);
	      }
	 }
	
}
