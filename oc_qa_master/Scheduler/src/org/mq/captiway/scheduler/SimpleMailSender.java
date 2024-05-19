package org.mq.captiway.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.DigitalReceiptUserSettings;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.LoyaltyBalance;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.captiway.scheduler.beans.LoyaltyThresholdBonus;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CustomTemplatesDao;
import org.mq.captiway.scheduler.dao.CustomTemplatesDaoForDML;
import org.mq.captiway.scheduler.dao.DigitalReceiptUserSettingsDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.LoyaltyBalanceDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.services.ExternalSMTPSender;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponCodesGeneration;
import org.mq.captiway.scheduler.utility.CouponProvider;
import org.mq.captiway.scheduler.utility.EncryptDecryptUrlParameters;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PrepareFinalHTML;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

/*import com.port25.pmta.api.submitter.Connection;
import com.port25.pmta.api.submitter.EmailAddressException;
import com.port25.pmta.api.submitter.Message;
import com.port25.pmta.api.submitter.Recipient;
import com.port25.pmta.api.submitter.ServiceException;*/

public class SimpleMailSender extends Thread {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private String vmta;
	//private Connection con=null;
	public volatile boolean isRunning ; 
	private final String serverName = Constants.PROPS_KEY_SERVERNAME;
	
	private Queue<EmailQueue> queue;
	private ApplicationContext context;
	private CouponCodesDao couponCodesDao ;
	private CouponsDao couponsDao ;
	private MessagesDao messagesDao ;
	private MessagesDaoForDML messagesDaoForDML ;
	private EmailQueueDao emailQueueDao ;
	private EmailQueueDaoForDML emailQueueDaoForDML ;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	
	
	
	public SimpleMailSender(ApplicationContext context, Queue<EmailQueue> queue) {
		this.context = context;
		this.queue = queue;
		
		 couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
		 couponsDao = (CouponsDao)context.getBean("couponsDao");
		 messagesDao = (MessagesDao)context.getBean("messagesDao");
		 messagesDaoForDML = (MessagesDaoForDML)context.getBean("messagesDaoForDML");
		 emailQueueDao = (EmailQueueDao)context.getBean("emailQueueDao");
		 emailQueueDaoForDML = (EmailQueueDaoForDML)context.getBean("emailQueueDaoForDML");
		
		
	}
	
	/*private void grabConnection() {
	
		final String server = PropertyUtil.getPropertyValueFromDB("SMTPHost");
		final String smtpUN = PropertyUtil.getPropertyValueFromDB("SMTPUserName");
		final String smtpPW = PropertyUtil.getPropertyValueFromDB("SMTPPassword");
		
		int smtpPort = 25;
		try {
			smtpPort = Integer.parseInt(PropertyUtil.getPropertyValueFromDB("SMTPPort"));
		} catch (NumberFormatException e1) {
			smtpPort = 25;
		}
		try {
			con = new Connection(server, smtpPort, smtpUN, smtpPW);
		} 
		catch (ServiceException se) {
			isRunning = false;
            logger.error("SMTP error while connecting to the server"+server, se);
            return;
        } 
        catch (IOException ioe) {
        	isRunning = false;
            logger.error("SMTP error while connecting to the server"+server, ioe);
            return;
        }

	}*/
	
	public void run() {
		
		try {
			isRunning = true;
			logger.info(" -------------------- just entered ------------");
			
			vmta = PropertyUtil.getPropertyValueFromDB("VMTA");
			
			String emailType = null;
			
			EmailQueue emailQueue = null;
			
			//Message msg = null;
			//Recipient rcpt = null;
			Campaigns campaign = null;
			Long campaignId = null;
			CustomTemplates custTemplate = null;
			Long customTempId = null;
			Contacts contact = null;
			String htmlContent = null;
			String textContent = null;
			Messages msgs = null;
			String from = null;
			String replyTo = null;
			String jobId =  null;
			String subject = null;
			Calendar dt = Calendar.getInstance();
			List<Messages> msgList = new ArrayList<Messages>();
			Set<String> totalPhSet = null;
			
			String messageStr = null;
			String logMsg = "";
			String messageHeader =  "";
			String prsnlizeToFld = 	"";
			String toEmailId = null;
			ServiceLocator locator = ServiceLocator.getInstance();
			CampaignsDao campaignsDao = (CampaignsDao)locator.getDAOByName(OCConstants.CAMPAIGNS_DAO);
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)locator.getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			
			while ((emailQueue =  queue.poll()) != null) {
				
				campaign = null;
				campaignId = null;
				custTemplate = null;
				customTempId = null;
				contact = null;
				subject = null; from = null; jobId = null;replyTo = null;
				textContent = null; htmlContent = null;
				msgs = null;
				Users user = null;
				List<UsersDomains> domainsList = null;
				String userDomainStr = "";
				
				try {
				user = emailQueue.getUser();
				
				if(user != null && !user.isEnabled()) {
					
					logger.info("user is disabled, could not send  "+emailQueue.getType() + " email. ");
					continue;
					
				}
				if(user != null) {
					UsersDao usersDao = (UsersDao)context.getBean("usersDao");
					domainsList = usersDao.getAllDomainsByUser(user.getUserId());
					Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
					if(domainsList != null) {
						domainSet.addAll(domainsList);
						for (UsersDomains usersDomains : domainSet) {
							
							if(userDomainStr.length()>0) userDomainStr+=",";
							userDomainStr += usersDomains.getDomainName();
							
						}
					}
				
				}
				
				//get contact object
				if(emailQueue.getContactId() != null) {
					
					if(logger.isInfoEnabled()) logger.info("contact has found...");
					ContactsDao contactsDao = (ContactsDao)context.getBean("contactsDao");
					
					contact = contactsDao.findById(emailQueue.getContactId());
					
				}
				
				toEmailId = emailQueue.getToEmailId();
					
					if( toEmailId == null || toEmailId.trim().equals("")) {
						
						if(emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL) ||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL) ||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_OC_ALERTS)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK_MAIL)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_SPECIAL_REWARDS)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ADJUSTMENT)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ISSUANCE)||
						   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REDEMPTION)) {
							
							
							
							if(contact != null) {
								
							if(contact.getEmailId() != null && 
									!contact.getEmailId().isEmpty()) {
								toEmailId = contact.getEmailId();
								emailQueue.setToEmailId(toEmailId);
								//emailQueueDao.saveOrUpdate(emailQueue);
								emailQueueDaoForDML.saveOrUpdate(emailQueue);
							}
							else {
								if(logger.isInfoEnabled()) logger.info("no 'TO' email found , returning....");
								emailQueue.setStatus(Constants.EQ_STATUS_FAILURE);
								//emailQueueDao.saveOrUpdate(emailQueue);
								emailQueueDaoForDML.saveOrUpdate(emailQueue);
								continue;
							}
							
							}
						}
						
						else {
							if(logger.isInfoEnabled()) logger.info("no 'TO' email found , returning....");
							continue;
						}
						
					}
					
					emailType = emailQueue.getType();
					
					if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_TESTMAIL)) {
						 campaignId = emailQueue.getCampaignId();
						 if(campaignId != null){
							 
							 campaign = campaignsDao.findByCampaignId(campaignId);
							 if(campaign == null){
								 
								 if(logger.isWarnEnabled()) logger.warn(" Found campaign as null for the campaign :"+
											+campaignId);
									continue;
							 }
						 }
					
						if(logger.isDebugEnabled()) {
							logger.debug(">>>>>>>>>> Trying to send Test mail(Campaign - "+
									campaign.getCampaignName()+")");
						}
					
						htmlContent = PrepareFinalHTML.prepareStuff(campaign, 
								emailQueue.getMessage(), null, true, userDomainStr);
					
						if(htmlContent == null) {
							if(logger.isWarnEnabled()) logger.warn(" Found Html content as null for the campaign :"+
									campaign.getCampaignName()+", Test mail can not be sent..");
							continue;
						}
					
						textContent = campaign.getTextMessage();
						if(textContent == null ) {
							if(logger.isWarnEnabled()) logger.warn(" No text content found for the campaing :"+
									campaign.getCampaignName());
							textContent = "";
						}
						//[Test]Campaign

						String Camp_Test=" [Test] ";
						jobId = Constants.EQ_TYPE_TESTMAIL;
						//from = emailQueue.getUser().getEmailId();
						from = campaign.getFromName() + "<" + campaign.getFromEmail() +">";
						replyTo = campaign.getReplyEmail();

						subject = Camp_Test+campaign.getSubject();


						//Code to replace symbols in subject line of test mail
						Set<String> symbolSet = getSymbolAndDateFields(subject+htmlContent+textContent);
						
						if(symbolSet != null && symbolSet.size()>0){
							
							subject = subject.replace("|^", "[").replace("^|", "]");
							
							for (String symbol : symbolSet) {
									if(symbol.startsWith(Constants.SYMBOL_PH_SYM)) {
										subject = subject.replace("["+symbol+"]", PropertyUtil.getPropertyValueFromDB(symbol));
									
									}
									/*else if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
										if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
											Calendar cal = MyCalendar.getNewCalendar();
											subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
											htmlContent = htmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									    	textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
										}
										else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
											Calendar cal = MyCalendar.getNewCalendar();
											cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
											subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
											htmlContent = htmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
									    	textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
										}
										else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
											
											try {
												String[] days = symbol.split("_");
												Calendar cal = MyCalendar.getNewCalendar();
												cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
												subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
												htmlContent = htmlContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
												textContent = textContent.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
											} catch (Exception e) {
												if(logger.isDebugEnabled()) logger.debug("exception in parsing date placeholder");
											}
										}//else if
									}*///else if
							}//for
						}//if
						
						// replace coupon ph with barcode images for test mail.
						//htmlContent = replaceCouponPhWithBarcode(htmlContent, user);
						
						
						/*if(symbolSet != null && symbolSet.size()>0){
							subject = subject.replace("|^", "[").replace("^|", "]");
								for(String symbol :symbolSet){
									if(symbol.startsWith("SYM_")) {
										logger.debug("symbol = "+symbol);
										subject = subject.replace("["+symbol+"]", PropertyUtil.getPropertyValueFromDB(symbol));
									}
								}
						}*/
						//replace symbols **********END***********
						textContent = textContent.replace("|^", "[").replace("^|", "]");
						totalPhSet = getCustomFields(htmlContent + textContent + subject);
						if(totalPhSet != null && totalPhSet.size() > 0) {
							
							ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
							
							String[] contentArr = replacePlaceHolders.replacePHForTestEmails(user, htmlContent, totalPhSet, subject, textContent);
							htmlContent = contentArr[0];
							textContent = contentArr[1];
							subject = contentArr[2];
							
						}
						
						htmlContent = htmlContent.replace("[email]",emailQueue.getToEmailId());
						htmlContent = htmlContent.replace("[eqId]", ""+emailQueue.getId());
						
						//set personalize field
						if(campaign.isPersonalizeTo() == true &&
								campaign.getToName() != null && 
										campaign.getToName().trim().length() > 0)	 {
							
								
							if(campaign.getToName().equals("firstName")){
								
								prsnlizeToFld =  user.getFirstName()==null?"":user.getFirstName();
								
							}else if(campaign.getToName().equals("lastName")){
								
								prsnlizeToFld =  user.getLastName()==null?"":user.getLastName();
							}else if(campaign.getToName().equals("fullName")) {
								
								prsnlizeToFld = user.getFirstName()==null?"":user.getFirstName();
								prsnlizeToFld += user.getLastName()==null?"":" "+user.getLastName();
								
							}
								
							
							
						}
						
						msgs = new Messages("Test campaign", "Test mail send success", 
								"Test mail is sent for the campaign '" + campaign.getCampaignName() +
								"'", dt,"Inbox", false, "INFO", emailQueue.getUser());
						
						logMsg = ">>>>>>>>> Test mail sent successfully (Campaign - "+
						campaign.getCampaignName()+") to :"+toEmailId;
					
							emailQueue.setMessage(htmlContent);
						//emailQueueDao.saveOrUpdate(emailQueue);
						emailQueueDaoForDML.saveOrUpdate(emailQueue);

					}else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_DIGITALRCPT)) {
						
						from =  user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
						replyTo = from;
						subject = emailQueue.getSubject();
						
						DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = 
								(DigitalReceiptUserSettingsDao)locator.getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
						DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
						
						if(digitalReceiptUserSettings != null) {
							
							String recieptSub = digitalReceiptUserSettings.getSubject();
							if(recieptSub != null &&
									recieptSub.trim().length() > 0) {
								String DR_Test=" [Test] ";

								subject = DR_Test+recieptSub;

							}
							
							//from email
							String recieptEmail = digitalReceiptUserSettings.getFromEmail();
							if(recieptEmail != null && 
									recieptEmail.trim().length() > 0) {
								
								from = recieptEmail;
							}
							
							//fromName
							String recieptFromName = digitalReceiptUserSettings.getFromName();
							if(recieptFromName != null && 
									recieptFromName.trim().length() > 0) {
								
								from = recieptFromName + "<" + from + ">";
								replyTo = from;
							}
							
							
							
						}
						htmlContent = emailQueue.getMessage();
						//Not required as subject is mandatory when they are sending test DR
						/*DigitalReceiptUserSettings digitalReceiptUserSettings  = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
						
						if(digitalReceiptUserSettings == null) {
							
							digitalReceiptUserSettings = new DigitalReceiptUserSettings();
							
						}*/
						htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
						totalPhSet = getCustomFields(htmlContent);
						totalPhSet.addAll(getSymbolAndDateFields(htmlContent));
						
						if(totalPhSet != null && totalPhSet.size() > 0) {
							
							ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
							
							String[] contentArr = replacePlaceHolders.replacePHForTestEmails(user, htmlContent, totalPhSet, null, null);
							
							htmlContent = contentArr[0];
							emailQueue.setMessage(htmlContent);
							//emailQueueDao.saveOrUpdate(emailQueue);
							emailQueueDaoForDML.saveOrUpdate(emailQueue);
							
							
						}
						
						StringBuffer sb = new StringBuffer(htmlContent);
						//sb.insert(0, "<HTML><HEAD></HEAD><BODY>");
						//sb.append("</BODY></HTML>");
						//************* for mobile templates************
						String testStr = "<DIV><span style='font-family: Arial,Helvetica,sans-serif; " +
								"font-weight: bold; color: rgb(255, 0, 0); padding-right: 3px; " +
								"padding-bottom: 15px; padding-left: 25px; display: block; font-size: 11pt;'>" +
								"This is a test e-Receipt for your preview.<br/></span></DIV>";
						sb.insert(0, testStr);
						String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
						//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
						sb.insert(0, mobileHeadStr);
						sb.append("</body></html>");
						
						htmlContent = sb.toString();
						
						textContent = htmlContent;
						
					}
					else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_OPTIN_MAIL)) {
						
						try {
							
							/*if( toEmailId == null || toEmailId.trim().equals("")) {
								
								logger.info("no 'TO' email found , returning....");
								return;
								
							}*/
							customTempId = emailQueue.getCustomTemplateId();
							logger.info("emailQueue.gettoemailid........."+emailQueue.getId());
							logger.info("emailQueue.gettoemailid........."+emailQueue.getToEmailId());
							logger.info("emailQueue.get subject........."+emailQueue.getSubject());
							logger.info("emailQueue.getcustomTempId........."+emailQueue.getCustomTemplateId());
							logger.info("emailQueue.getuser........."+emailQueue.getUser());
							
							if(customTempId != null){
								
								custTemplate = customTemplatesDao.findCustTemplateById(customTempId);
							}
							
							String Auto_Test=" [Test] ";

							from =  user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
							replyTo = from;

							subject = Auto_Test+emailQueue.getSubject();
							htmlContent = emailQueue.getMessage();
							 
							if(custTemplate != null) {
								
								from =  custTemplate.getFromName() + "<"+ custTemplate.getFromEmail()+">";
								subject = custTemplate.getSubject();
								htmlContent = custTemplate.getHtmlText();
								replyTo = custTemplate.getReplyToEmail();
								
								
							}
							
							
							if(logger.isDebugEnabled()) {
								logger.debug(">>>>>>>>>> Trying to send  Double Opt-in Test mail(Campaign - "
										);
							}
							
							htmlContent = PrepareFinalHTML.prepareDoubleOptInStuff(custTemplate, true, htmlContent, emailQueue.getUser(), userDomainStr);
							htmlContent = htmlContent.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
							htmlContent = htmlContent.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName());
							htmlContent = htmlContent.replace("[senderName]", user.getFirstName());
							htmlContent = htmlContent.replace("[email]",emailQueue.getToEmailId());
							/*htmlContent = htmlContent.replace("[First_Name]",emailQueue.getToEmailId());
							htmlContent = htmlContent.replace("[Date_of_Birth]",emailQueue.getToEmailId());
							htmlContent = htmlContent.replace("[Email_ID]",emailQueue.getToEmailId());
							*/
							
							//code to replace date placeholders in auto emails
							htmlContent = replaceDatePh(htmlContent, user);
							//htmlContent = replaceCouponPhWithBarcode(htmlContent, user);
							
							
							if(textContent == null ) {
								if(logger.isWarnEnabled()) logger.warn(" No text content found for the campaing :"
										);
								textContent = "";
							}
							
							jobId = Constants.EQ_TYPE_TEST_OPTIN_MAIL;
							
							msgs = new Messages("Test campaign", "Test mail send success", 
									"Test mail is sent for the campaign '" + 
									"'", dt,"Inbox", false, "INFO", emailQueue.getUser());
							
							logMsg = ">>>>>>>>> Test mail sent successfully  to : "+emailQueue.getToEmailId();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						}
						
					}
					//String EQ_TYPE_TEST_PARENTAL_MAIL = "TestParentalMail";

					else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_PARENTAL_MAIL) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL) || 
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_WELCOME_MAIL) ||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL) ||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL)||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL)||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL)||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL)||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL)||
							emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_OC_ALERTS) || 
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_SPECIAL_REWARDS)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ADJUSTMENT)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ISSUANCE)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REDEMPTION)||
								emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_ISSUE_COUPON)){
						//even this is being processed as a test mail these are actually the main emails which reaches to contact.
						//the actual test mail will be sent as of type 'double opt in' as the processing logic is same under test email,
						//thats why istestmail flag always we can keep as 'false' for these two types.
						try {
							
							
							/*if(emailQueue.getToEmailId().trim().equals("") || emailQueue.getToEmailId() == null) {
								
								logger.info("no 'TO' email found , returning....");
								return;
								
							}
							*/
							
							String greetings = "Dear sir/madam, ";
							
							//custTemplate = emailQueue.getCustomTemplates();
							customTempId = emailQueue.getCustomTemplateId();
							if(customTempId != null){
								
								custTemplate = customTemplatesDao.findCustTemplateById(customTempId);
							}
							
							from = user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
							replyTo = from;
							subject = emailQueue.getSubject();
							htmlContent = emailQueue.getMessage();
							//added
							textContent = emailQueue.getMessage();
							if(custTemplate != null) {
								
								from = custTemplate.getFromName() + "<"+ custTemplate.getFromEmail()+">";
								subject = custTemplate.getSubject();
								replyTo = custTemplate.getReplyToEmail();
								//htmlContent = custTemplate.getHtmlText();
								
								
							}
							
							String url = PropertyUtil.getPropertyValue("parentalFormPdfUrl");
							
							Long contactId = emailQueue.getContactId();
							if(contactId != null) {
								
								if(logger.isInfoEnabled()) logger.info("no contact has found...");
								ContactsDao contactsDao = (ContactsDao)context.getBean("contactsDao");
								
								contact = contactsDao.findById(contactId);
								
							}
							if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL) ||
							   emailType.equalsIgnoreCase(Constants.EQ_TYPE_WELCOME_MAIL) ||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL) ||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_OC_ALERTS) ||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK_MAIL)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_SPECIAL_REWARDS)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ADJUSTMENT)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ISSUANCE)||
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REDEMPTION) || 
							   emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_ISSUE_COUPON)) {/* ||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_PARENTAL_MAIL)) {
								*/
								url = PropertyUtil.getPropertyValue("fetchLoyaltyBalenceUrl");
								
								
								
								if(custTemplate != null) {
									
										if(custTemplate.isPersonalizeTo() == true &&
												custTemplate.getToName() != null && 
												custTemplate.getToName().trim().length() > 0)	 {
											
											
												if(contact != null) {
													
													if(custTemplate.getToName().equals("firstName")){
														
														prsnlizeToFld =  contact.getFirstName()==null?"":contact.getFirstName();
														
													}else if(custTemplate.getToName().equals("lastName")){
														
														prsnlizeToFld =  contact.getLastName()==null?"":contact.getLastName();
													}else if(custTemplate.getToName().equals("fullName")) {
														
														prsnlizeToFld = contact.getFirstName()==null?"":contact.getFirstName();
														prsnlizeToFld += contact.getLastName()==null?"":" "+contact.getLastName();
														
													}
												}
												
										}//if
										
									}//if
								if(contact != null && !emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_OC_ALERTS)) {
									greetings = contact.getFirstName() == null ? greetings : " Hi "+contact.getFirstName()+","; 
								}
								
							}//if
					
							
							if(logger.isDebugEnabled()) {
								logger.debug(">>>>>>>>>> Trying to send  Double Opt-in Test mail(Campaign - "
										+emailQueue.getChildEmail());
							}
							
							if(emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_OC_ALERTS) ||
									emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL)) {
								htmlContent = emailQueue.getMessage();
							}
							else{
								htmlContent = htmlContent.replace("[url]", url );
								htmlContent = PrepareFinalHTML.prepareParentalOptInStuff(custTemplate, false, htmlContent, emailQueue.getUser(), userDomainStr);
								if(custTemplate != null){
								Set<String> urlSet = custTemplate.getUrls();
								if(urlSet == null)urlSet = new HashSet<String>(); 
								urlSet.addAll(getUrls(htmlContent));
								custTemplate.setUrls(urlSet);
								CustomTemplatesDaoForDML customTemplatesDaoForDML = (CustomTemplatesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("customTemplatesDaoForDML");
								//customTemplatesDao.saveOrUpdate(custTemplate);
								customTemplatesDaoForDML.saveOrUpdate(custTemplate);
								}
								//emailQueue.setDeliveryStatus("Success");
								emailQueue.setDeliveryStatus(Constants.EQ_STATUS_SUBMITTED);
							}
							
							htmlContent = htmlContent.replace("[Organization_Name]", user.getUserOrganization().getOrganizationName());
							htmlContent = htmlContent.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
							htmlContent = htmlContent.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName());
							htmlContent = htmlContent.replace("[senderName]", user.getFirstName());
							htmlContent = htmlContent.replace("[email]",emailQueue.getToEmailId());
							htmlContent = htmlContent.replace("[First_Name]",emailQueue.getChildFirstName()!= null ? emailQueue.getChildFirstName() : "" );
							htmlContent = htmlContent.replace("[Date_of_Birth]",emailQueue.getDateOfBirth() != null ? emailQueue.getDateOfBirth() : "");
							htmlContent = htmlContent.replace("[Email_ID]",emailQueue.getChildEmail() != null ? emailQueue.getChildEmail() : "");
							htmlContent = htmlContent.replace("[url]", url );
							htmlContent = htmlContent.replace("[greetings]", greetings );
							htmlContent = htmlContent.replace("[senderReplyToEmailID]", user.getEmailId());
							htmlContent = htmlContent.replace("[eqId]", ""+emailQueue.getId());
							
							
//************************************* Manage auto emails place holders processing *****************
							
							String campaignName=null;
							
							if(custTemplate!=null) campaignName=custTemplate.getTemplateName();
							
							htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
							subject = subject.replace("|^", "[").replace("^|", "]");
							
							//**********date ph code
							htmlContent = replaceDatePh(htmlContent, user);
							//subject = replaceDatePh(subject);
							
							totalPhSet = getCustomFields(htmlContent + subject);
							
							if(totalPhSet != null && totalPhSet.size() > 0) {
								
								boolean canProceed = canProceedForPhWithCoupons(totalPhSet, emailType, user);
								if(!canProceed) {
									
									emailQueue.setStatus(Constants.EQ_STATUS_PAUSE);
									//emailQueueDao.saveOrUpdate(emailQueue);
									emailQueueDaoForDML.saveOrUpdate(emailQueue);
									continue;//continue for next EQ object.
									
								}
							
							
							}
							campaignId = emailQueue.getCampaignId();
							 if(campaignId != null){
								 
								 campaign = campaignsDao.findByCampaignId(campaignId);
								 if(campaign == null){
									 
									 if(logger.isWarnEnabled()) logger.warn(" Found campaign as null for the campaign :"+
												+campaignId);
										continue;
								 }
							 }
							
							ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(context, 
									user.getUserOrganization().getUserOrgId(), null, campaignName);

							if(totalPhSet != null && totalPhSet.size() >0) {
								
								if(contact != null)contact.setTempObj(new Long(emailQueue.getId())); // setting Id 
								logger.debug("Inside the placeholders");
								String [] contentsStrArr = replacePlaceHolders.getContactPhValue(contact, htmlContent,
										"", subject, totalPhSet, Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL, toEmailId, user, 
										new Long(emailQueue.getId()),emailQueue.getLoyaltyId());
								logger.debug("Value returned after replacing");
								htmlContent = contentsStrArr[0];
								//tempTextContent = contentsStrArr[1];
								subject = contentsStrArr[2];
								
								//after completion of this operation flush the coupon vector 
								try {
									Utility.updateCouponCodeCounts(context, totalPhSet);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("** Exception while updating the coupons", e);
								}
								/*CouponProvider couponProvider = CouponProvider.getCouponProviderInstance(context);
						    		couponProvider.flushCouponCodesToDB(true);*/
								
							}
							
							
							//htmlContent = checkBarcodeEmpty(htmlContent);
							
							
//***********************************************************************************************
							
							
							
							emailQueue.setMessage(htmlContent);
							//emailQueueDao.saveOrUpdate(emailQueue);
							emailQueueDaoForDML.saveOrUpdate(emailQueue);
							
							if(textContent == null ) {
								if(logger.isWarnEnabled()) logger.warn(" No text content found for the campaing :"	);
								textContent = "";
							}
							
							jobId = Constants.EQ_TYPE_TEST_PARENTAL_MAIL;
							
							msgs = new Messages("Test campaign", "Test mail send success", 
									"Test mail is sent for the campaign '" + 
									"'", dt,"Inbox", false, "INFO", emailQueue.getUser());
							
							logMsg = ">>>>>>>>> Test mail sent successfully  to : "+emailQueue.getToEmailId();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						}
						
					}
					else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_SUPPORT_ALERT)) {
						subject = emailQueue.getSubject();
						logMsg = ">>>>>>>>>> "+emailQueue.getType()+" message sent ";
						htmlContent = emailQueue.getMessage();
						textContent = emailQueue.getMessage(); 
						jobId = Constants.EQ_TYPE_SUPPORT_ALERT;
						from = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
						replyTo = null;
						
					}
					else if(emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_OTP_MAIL)){
						subject = emailQueue.getSubject();
						logMsg = ">>>>>>>>>> "+emailQueue.getType()+" message  sent ";
						htmlContent = emailQueue.getMessage();
						textContent = emailQueue.getMessage(); 
						jobId = Constants.EQ_TYPE_OTP_MAIL;
						from = user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
						replyTo = from;
						logger.info("Inside OPT Email From mail :"+from);
					}
					else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK)||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_FORGOT_PASSWORD) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY) || 
							emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT) || 
							emailType.equals(Constants.EQ_STATUS_RESUBSCRIPTION)||
							emailType.equals(Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS) ||
							emailType.equals(Constants.EQ_TYPE_KEYWORD_ALERT) ||
							emailType.equals(Constants.EQ_TYPE_NEW_USER_DETAILS) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPTIN_REQUEST_TYPE) || 
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_PROMO_REDEMTION) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ENROLL) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ISSUANCE) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_DIGI_RECEIPT) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_FILE_EXPORT)) {
						
						subject = emailQueue.getSubject();
						logMsg = ">>>>>>>>>> "+emailQueue.getType()+" message sent ";
						htmlContent = emailQueue.getMessage();
						textContent = emailQueue.getMessage(); 
						
						
						if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_DIGITALRECIEPT) || emailType.equals(Constants.EQ_STATUS_RESUBSCRIPTION)) {
							from = emailQueue.getUser().getEmailId();
							replyTo = from;
							jobId = Constants.EQ_TYPE_DIGITALRECIEPT;
							
							if( emailType.equalsIgnoreCase(Constants.EQ_TYPE_DIGITALRECIEPT) ) {
								
							DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)context.getBean("digitalReceiptUserSettingsDao");	
								
								DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
								
								if(digitalReceiptUserSettings != null) {
									
									//subject
									String recieptSub = digitalReceiptUserSettings.getSubject();
									if(recieptSub != null &&
											recieptSub.trim().length() > 0) {
										
										subject = recieptSub;
									}
									
									//from email
									String recieptEmail = digitalReceiptUserSettings.getFromEmail();
									if(recieptEmail != null && 
											recieptEmail.trim().length() > 0) {
										
										from = recieptEmail;
									}
									
									//fromName
									String recieptFromName = digitalReceiptUserSettings.getFromName();
									if(recieptFromName != null && 
											recieptFromName.trim().length() > 0) {
										
										from = recieptFromName + "<" + from + ">";
									}
									
									
									//set personalize field
									if(digitalReceiptUserSettings.isPersonalizeTo() == true &&
											digitalReceiptUserSettings.getToName() != null && 
													digitalReceiptUserSettings.getToName().trim().length() > 0)	 {
										Long contactId = emailQueue.getContactId();
										
										if(contactId != null) {
											
											if(logger.isInfoEnabled()) logger.info("no contact has found...");
											ContactsDao contactsDao = (ContactsDao)context.getBean("contactsDao");
											
											contact = contactsDao.findById(contactId);
											if(contact != null) {
												
												if(custTemplate.getToName().equals("firstName")){
													
													prsnlizeToFld =  contact.getFirstName()==null?"":contact.getFirstName();
													
												}else if(custTemplate.getToName().equals("lastName")){
													
													prsnlizeToFld =  contact.getLastName()==null?"":contact.getLastName();
												}else if(custTemplate.getToName().equals("fullName")) {
													
													prsnlizeToFld = contact.getFirstName()==null?"":contact.getFirstName();
													prsnlizeToFld += contact.getLastName()==null?"":" "+contact.getLastName();
													
												}
											}
										}//if contact not null
										
										
									}//if
									
									
								}
								
								
							}//if
							
							
							
						}

						if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK) || emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPTIN_REQUEST_TYPE)) {
							if(logger.isDebugEnabled()) {
								logger.debug(">>>>>>>>>> Trying to send feedback");
							}
							from = emailQueue.getUser().getEmailId();
							replyTo = from;
							
							if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPTIN_REQUEST_TYPE)){
								
								jobId = Constants.EQ_TYPE_OPTIN_REQUEST_TYPE;
								
							}else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK)){
								
								jobId = Constants.EQ_TYPE_FEEDBACK;
							}
						}
						else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_FORGOT_PASSWORD) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY)||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS) ||
								emailType.equalsIgnoreCase(Constants.KEYWORD_EXP_MAIL_TYPE)||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_NEW_USER_DETAILS) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_PROMO_REDEMTION) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ENROLL) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ISSUANCE) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_DIGI_RECEIPT) ||
								emailType.equalsIgnoreCase(Constants.EQ_TYPE_FILE_EXPORT)){
								//emailType.equalsIgnoreCase(Constants.EQ_TYPE_SUPPORT_ALERT)) {
							
							//here we should change it as support@optculture.com
							String fromMailIdStr = PropertyUtil.getPropertyValue(
									Constants.PROPS_KEY_SUPPORT_EMAILID);
							from = fromMailIdStr.substring(fromMailIdStr.indexOf('<')+1,
									fromMailIdStr.lastIndexOf('>'));
							replyTo = from;
							
							from = "OptCulture Support"+"<"+from+">"; // 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place1
							if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_FORGOT_PASSWORD)) {
								jobId = Constants.EQ_TYPE_FORGOT_PASSWORD;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS)) {
								jobId = Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_NEW_USER_DETAILS)) {
								jobId = Constants.EQ_TYPE_NEW_USER_DETAILS;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY)) {
								jobId = Constants.EQ_TYPE_USER_MAIL_VERIFY;
								messageStr = "The new Email verification mail" +
								" has been sent successfully \r\n" ;
								msgs = new Messages("Email Verification", 
										"Email verification mail sent ", messageStr,
										dt,  "Inbox", false, "Info", user);
							}else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_PROMO_REDEMTION) || 
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_FILE_EXPORT)) {
								jobId = Constants.EQ_TYPE_OPT_SYN_PROMO_REDEMTION;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ENROLL)) {
								jobId = Constants.EQ_TYPE_OPT_SYN_LOYALTY_ENROLL;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_LOYALTY_ISSUANCE)) {
								jobId = Constants.EQ_TYPE_OPT_SYN_LOYALTY_ISSUANCE;
							}
							else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_OPT_SYN_DIGI_RECEIPT)) {
								jobId = Constants.EQ_TYPE_OPT_SYN_DIGI_RECEIPT;
							}
							/*else if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_SUPPORT_ALERT)) {
								jobId = Constants.EQ_TYPE_SUPPORT_ALERT;
								from = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
							}*/
							logMsg = ">>>>>>>>>> "+emailQueue.getType()+" message sent ";
						}
					}
							
							JSONObject messageHEaderObject = new JSONObject();
							JSONArray toEmailArray = new JSONArray();
							JSONObject uniqueArgsObject = new JSONObject();
							
							
							//add all the email addresses in the to email id field.
							
							if( (!toEmailId.isEmpty()) && toEmailId.contains(Constants.ADDR_COL_DELIMETER)) {
								
									String mailIdsArray[] = toEmailId.split(Constants.ADDR_COL_DELIMETER);
									
									for(String mailId : mailIdsArray) {
										
										toEmailArray.add(mailId);
									}
									
							}
							else	toEmailArray.add( toEmailId);
							
							uniqueArgsObject.put("ServerName", serverName);
							uniqueArgsObject.put("Email", emailType);
							uniqueArgsObject.put("EQID", ""+emailQueue.getId());
							
							
							if(user != null) {
								
								uniqueArgsObject.put("userId", user.getUserId());
								
							}
							
							//Added for autoEMail report
							if(		emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_PARENTAL_MAIL) || 
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL) || 
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_WELCOME_MAIL) ||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL) ||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL) ||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL)||
									 emailQueue.getType().equalsIgnoreCase(Constants.EQ_TYPE_FEEDBACK_MAIL)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_SPECIAL_REWARDS)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ADJUSTMENT)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_ISSUANCE)||
									emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOYALTY_REDEMPTION)){
								
								htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(emailQueue.getId()+""));
								
								if(textContent != null  ){
									textContent = textContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(emailQueue.getId()+""));
								}//if
								
								if(user != null) {
									uniqueArgsObject.put("userId", user.getUserId());
								}
								uniqueArgsObject.put("EmailType", Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL);
								uniqueArgsObject.put("sentId", ""+emailQueue.getId());
								uniqueArgsObject.put("ServerName", serverName);
								emailQueue.setCustTempName(custTemplate!=null ? custTemplate.getTemplateName():null);
								//emailQueueDao.saveOrUpdate(emailQueue);
								emailQueueDaoForDML.saveOrUpdate(emailQueue);
							}
							//end
							
							messageHEaderObject.put("unique_args", uniqueArgsObject);
							messageHEaderObject.put("to", toEmailArray);
							
							messageHeader = messageHEaderObject.toString();
							
										
					if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_LOW_SMS_CREDITS) ) {
						
						htmlContent = emailQueue.getMessage();
						if(textContent == null  ){
							
							textContent = "";
							
						}//if
						
						from = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
						
						
						replyTo = from;
						subject = emailQueue.getSubject();
						//messageHeader =  "{\"unique_args\": {\"Email\": \""+ emailType +"\",  \"ServerName\": \""+ serverName +"\" }}";
						from = "OptCulture Support"+"<"+from+">"; // 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place2
						
					}//else if
					
					if(	emailType.equalsIgnoreCase(Constants.EQ_TYPE_SALES_DATA_NOT_RECEIVED_ALERT) ||
							emailType.equalsIgnoreCase(Constants.EQ_TYPE_WEEKLY_CAMP_REPORT) || 
							emailType.equalsIgnoreCase(Constants.LOYALTY_DAILY_WEEKLY_REPORT) ||
							emailType.equalsIgnoreCase(OCConstants.LOYALTY_FRAUD_ALERT_REPORT) ||
							emailType.equalsIgnoreCase(Constants.CONTACTS_FILE_UPLOAD_FAILED) ||
							emailType.equalsIgnoreCase(Constants.SKU_FILE_UPLOAD_FAILED) || 
							emailType.equalsIgnoreCase(Constants.SALES_FILE_UPLOAD_FAILED)) {
						
						htmlContent = emailQueue.getMessage();
						if(textContent == null  ){
							
							textContent = "";
							
						}//if
						
						from = PropertyUtil.getPropertyValueFromDB("AlertFromEmailId");
						
						
						replyTo = from;
						subject = emailQueue.getSubject();
						//messageHeader =  "{\"unique_args\": {\"Email\": \""+ emailType +"\",  \"ServerName\": \""+ serverName +"\" }}";

						
					}
					
					 
					// Sending throught sendGrid if the user is configured to it....
					if(vmta.equalsIgnoreCase("SendGridAPI")) {
						
						try {
							
							if(logger.isDebugEnabled()) logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>"+messageHeader);
							ExternalSMTPSender externalSMTPSender =  new ExternalSMTPSender(context);
							
							if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_SALES_DATA_NOT_RECEIVED_ALERT)) {
								
								logger.info("submitting mail to multiple address ");
								
								externalSMTPSender.submitEmailToMultipleAddress(messageHeader, htmlContent,from,replyTo,
										subject, emailQueue.getToEmailId(),emailQueue.getCcEmailId());
								
								logger.info("exit submitting mail to multiple address ");
							}
							else

								externalSMTPSender.submitSimpleEmail(messageHeader, htmlContent, textContent, from,replyTo, subject, emailQueue.getToEmailId(),emailQueue.getCcEmailId(), prsnlizeToFld, emailType);
							
							continue;
						} catch (Exception e) {
							
							if(logger.isDebugEnabled()) logger.debug("Exception while sending through sendGridAPI .. returning ",e);
							continue;
						}
					}	
					
					/*if(from == null && jobId ==null) continue;
					
					logMsg = ">>>>>>>>>> "+emailQueue.getType()+" message sent ";
						
					
					msg = buildMail(from, subject, htmlContent, textContent);//new Message(fromMailIdStr);
					msg.setJobId(jobId);
					msg.setVirtualMta(vmta);
					rcpt = new Recipient(emailQueue.getToEmailId());
					msg.addRecipient(rcpt);
					
					if(con==null) grabConnection();
					
					con.submit(msg);*/

					if(msgs != null ) {
						msgList.add(msgs);
					}
					
				if(logger.isDebugEnabled()) {
					logger.debug(logMsg);
				}
				

				}/*catch (EmailAddressException e) {
					messageStr = "Invalid Email Address";
					if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY)) {
						messageStr = "The new Email verification mail has failed. \r\n" ;
						msgs = new Messages("Email Verification", "From-Email verification failed ", 
								messageStr, dt,  "Inbox", false, "Info", user);
						msgList.add(msgs);
					}
					logger.warn(" Invalid Email Address :"+emailQueue.getUser().getEmailId(), e);
				} catch (IOException e) {
					messageStr = "Network problem";
					if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY)) {
						messageStr = "The new Email verification mail has failed. \r\n" ;
						msgs = new Messages("Email Verification", "Email verification failed ",
								messageStr, dt,  "Inbox", false, "Info", user);
						msgList.add(msgs);
					}
					logger.warn(" IO Excption ", e);
				} catch (ServiceException e) {
					messageStr = "Server problem(IO)";
					if(emailType.equalsIgnoreCase(Constants.EQ_TYPE_USER_MAIL_VERIFY)) {
						messageStr = "The new Email verification mail has failed. \r\n" ;
						msgs = new Messages("Email Verification", "From-Email verification failed ", 
								messageStr, dt,  "Inbox", false, "Info", user);
						msgList.add(msgs);
					}
					logger.warn(" ServiceException ",e);
				}*/
				catch(Exception e) {
					logger.error(" Exception ",e);
				}
				
			}
			
			/*if(con!=null) con.close();
			
			if(logger.isDebugEnabled()) {
				logger.debug(" Connection closed ");
			}
			*/
			if(msgList.size() > 0) {
				try {
					//((MessagesDao)context.getBean("messagesDao")).saveByCollection(msgList);
					((MessagesDaoForDML)context.getBean("messagesDaoForDML")).saveByCollection(msgList);

				} catch (BeansException e) {
					if(logger.isWarnEnabled()) logger.warn(" MessagesDao bean could not get to store the new msgs ", e);
				} catch(Exception e) {
					if(logger.isErrorEnabled()) logger.error("** Exception : while storing the messages ", e);
				}
			}
			isRunning = false;
			if(logger.isInfoEnabled()) logger.info("------------------ Exiting ---------------------");
			
		} catch (Exception e) {
			logger.error(" Exception ",e);
		}
		finally {
			
			isRunning = false;
		}
	}
	
	
	/*public Message buildMail(String from, String subject, String htmlContent, String textContent) {
		Message msg = null;
		  try {
			  String outerBoundary = "outerBoundery";
			  String innerBoundary = "innerBoundery";
			  msg = new Message(from);
              
              String headers = 
            	  "From: [*from]\n" +
            	  "To: [*to]\n" +
                  "Subject: " + subject + "\n" +
                  "MIME-Version: 1.0\n" +
                  "Content-Type: multipart/related; boundary=\"" 
                      + outerBoundary + "\"\n";
              msg.addMergeData(headers.getBytes()); 
              msg.addDateHeader();

              // Optionally add a preamble for old mail clients that don't 
              // know MIME:
              String preamble =                     
                  "\n" +
                  "This is a multi-part message in MIME format.\n" +
                  "\n";
              msg.addMergeData(preamble.getBytes());
              
              String innerPart = 
                  "--" + outerBoundary + "\n" +
                  "Content-Type: multipart/alternative; boundary=\"" 
                      + innerBoundary + "\"\n" +
                  "\n";
              msg.addMergeData(innerPart.getBytes());
              
              String plainTextBody =
                  "--" + innerBoundary + "\n" +
                  "Content-Type: text/plain; charset=us-ascii\n" +
                  "\n" +
                  textContent+"\n";
              msg.addMergeData(plainTextBody.getBytes());
             
              String htmlBody =
                  "--" + innerBoundary + "\n" +
                  "Content-Type: text/html; charset=us-ascii\n" +
                  "\n" +
                  "<html>\n" +
                  "<body>\n" +
                  htmlContent+"\n"+
                  "</body>\n" +
                  "</html>\n";
              msg.addMergeData(htmlBody.getBytes());

              String partEnd =
                  "\n--" + innerBoundary + "--\n";
              msg.addMergeData(partEnd.getBytes());

              msg.setEncoding(Message.ENCODING_7BIT);
              
              String endOfMessage = 
                  "\n--" + outerBoundary + "--\n";
              msg.addMergeData(endOfMessage.getBytes());
          } catch (Exception e) {
			logger.error("Exception : ", e);
		}
          return msg;
	}*/
	
	private Set<String> getCustomFields(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();
	
		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				logger.info("Ph holder :" + ph);

				if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("CF_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("MLS_")){
					totalPhSet.add(ph);

				}
				else if(ph.startsWith(Constants.DATE_PH_DATE_)){
					totalPhSet.add(ph);
				}
				
			} // while
			
			if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception while getting the place holders ", e);
		}

		
		return totalPhSet;
	}
	
	//************* Code to replace Date ph of auto emails*****************
	private String replaceDatePh(String content, Users user){
		
		Set<String> symbolSet = getSymbolAndDateFields(content);
		if(symbolSet != null && symbolSet.size()>0){
			for (String symbol : symbolSet) {
					 if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
							Calendar cal = MyCalendar.getNewCalendar();
							content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						}
						else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
							content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						}
						else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
							
							try {
								String[] days = symbol.split("_");
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
								content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} catch (Exception e) {
								if(logger.isDebugEnabled()) logger.debug("exception in parsing date placeholder");
							}
						}
					}//if
			}//for
		}//if
		
		return content;
		
		
	}
	
	//************* END**********************************************************************
	
	
	private Set<String> getSymbolAndDateFields(String content) {
	
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
				else if(ph.startsWith(Constants.COUPON_CC)){
					subjectSymbolSet.add(ph);
				}
				
			} // while
			
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception while getting the symbol place holders ", e);
		}

		if(logger.isInfoEnabled()) logger.info("symbol PH  Set : "+ subjectSymbolSet);

		return subjectSymbolSet;
	}
	
	/*
	 * This replaces coupon placeholders with barcode images for Test mails only
	 */
	private String replaceCouponPhWithBarcode(String htmlContent, Users user){

		Set<String> symbolSet = getSymbolAndDateFields(htmlContent);
		
		if(symbolSet != null && symbolSet.size()>0){
		
		for (String symbol : symbolSet) {
			String[] phStr = symbol.split("_");
			
			if(symbol.startsWith("CC_") && phStr.length == 6){
			
			try{
				logger.debug("Inside replacing coupon ph: "+symbol);
			int width = Integer.parseInt(phStr[4].trim());
			int height = Integer.parseInt(phStr[5].trim());
			
			BitMatrix bitMatrix = null;
			
			String COUPON_CODE_URL = null;
			String ccPreviewUrl = null;
			
			String message = "Test:"+phStr[2];
			
			if(phStr[3].equals(Constants.COUP_BARCODE_QR)){
				bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
				String bcqrImg = user.getUserName()+File.separator+
						"Preview"+File.separator+"QRCODE"+File.separator+symbol+".png";
				
				COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
				
				ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
			}
			else if(phStr[3].equals(Constants.COUP_BARCODE_AZTEC)){
				
				bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
				String bcazImg = user.getUserName()+File.separator+
						"Preview"+File.separator+"AZTEC"+File.separator+symbol+".png";
				
				COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
				
				ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
			}
			else if(phStr[3].equals(Constants.COUP_BARCODE_LINEAR)){
				
				bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
				String bclnImg = user.getUserName()+File.separator+
						"Preview"+File.separator+"LINEAR"+File.separator+symbol+".png";
				
				COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
				
				ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
			}
			else if(phStr[3].equals(Constants.COUP_BARCODE_DATAMATRIX)){
				
				bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
				String bcdmImg = user.getUserName()+File.separator+
						"Preview"+File.separator+"DATAMATRIX"+File.separator+symbol+".png";
				
				COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
				
				ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
			}
			
			if(bitMatrix == null){
				return htmlContent;
			}
			File myTemplateFile = new File(COUPON_CODE_URL);
			File parentDir = myTemplateFile.getParentFile();
			 if(!parentDir.exists()) {
					
					parentDir.mkdir();
				}

			
			if(!myTemplateFile.exists()) {
				
				MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
	            		new File(COUPON_CODE_URL)));	
			}
			
			
			htmlContent = htmlContent.replace("["+symbol+"]",
					"<img width='"+width+"' height= '"+height+"' src= '"+ccPreviewUrl+"' />");
			
			}
			catch(Exception e){
				logger.error("Exception ::::" , e);
				logger.error("Invalid coupon placeholder parameters...");
				return htmlContent;
			}
		}// if CC_
	
		}// for
		
	 }//if
		return htmlContent;
	}//replaceCouponPhWithBarcode

	private String checkBarcodeEmpty(String htmlContent){
		try{
		String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
		String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
		String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
		String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
		String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
		String altPattern = "<img .*?alt\\s*?=\\\"?(.*?)\\\".*?>";
		
		Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(htmlContent);
		
		
		while(matcher.find()) {

			String imgtag = null;
			//String srcAttr = null;
			String idAttr = null;
			String wAttr = null;
			String hAttr = null;
			String altAttr = null;
			
				imgtag = matcher.group();
				
				/*Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
				Matcher srcm = srcp.matcher(imgtag);*/
				
				Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
				Matcher idm = idp.matcher(imgtag);
				
				Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
				Matcher widthm = widthp.matcher(imgtag);
				
				Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
				Matcher heightm = heightp.matcher(imgtag);
				
				Pattern altp = Pattern.compile(altPattern, Pattern.CASE_INSENSITIVE);
				Matcher altm = heightp.matcher(imgtag);
				
				
				/*while(srcm.find()){
					srcAttr = srcm.group(1);
				}*/
				while(idm.find()){
					idAttr = idm.group(1);
				}
				while(widthm.find()){
					wAttr = widthm.group(1);
				}
				while(heightm.find()){
					hAttr = heightm.group(1);
				}
				while(altm.find()){
					altAttr = altm.group(1);
				}
				
				if(altAttr == null || altAttr.trim().length() == 0){
					
					htmlContent = htmlContent.replace(imgtag,"");
				}
				
				
				logger.debug("htmlContent with barcode = "+htmlContent);
				
		} 
			
	}catch(Exception e){
		logger.error("Exception ::::" , e);
		logger.error("** Exception in replacing coupon img tag with barcode mqrm **");
		return htmlContent;
	}
		return htmlContent;
	}
	
	
	/**
	 * Get the URLs
	 * @param htmlContent
	 * @return Set of URL strings
	 */
	private Set<String> getUrls(String htmlContent) {


		int options = 0;
		options |= 128; 	//This option is for Case insensitive
		options |= 32;
		Set<String> urlSet = new HashSet<String>();

		if(htmlContent==null) return urlSet;

		try{
			
		    Pattern p = Pattern.compile(PropertyUtil.getPropertyValue("LinkPattern"), options);
		    Matcher m = p.matcher(htmlContent);
		    String anchorUrl;

	        while (m.find()) {

	        	anchorUrl = m.group(2).trim();
	            if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
	            	continue;
	            }
	            else if(anchorUrl.contains("action=click")) {
	            	anchorUrl = anchorUrl.substring(anchorUrl.lastIndexOf("url=")+4);
	            	if(logger.isInfoEnabled()) logger.info("URL is: " + anchorUrl);
					urlSet.add(anchorUrl);
	            }
	        } //while

		}
		catch (Exception e) {
			logger.error("** Exception : Problem while getting the URL set ", e);
		}
		return urlSet;

	} // getUrls
	
	
	
	/**
	 * added to not to send email if coupons r insufficient.
	 * @param totalPhSet
	 * @param emailType
	 * @param user
	 * @return
	 */
	public boolean canProceedForPhWithCoupons(Set<String> totalPhSet, String emailType, Users user) {
		
		//********************BEGIN COUPON CREDITS CHECKING*******************************
		
		//make a decission whether coupon codes will be sufficient or not
		
		String msgStr = Constants.STRING_NILL;
		
		
				boolean success = true;
				Coupons coupon = null;
				Long couponId = null; 
				try {
				for (String eachPh : totalPhSet) {
					if(!eachPh.startsWith("CC_") ) continue;
					//only for CC
						
						
						String[] strArr = eachPh.split("_");
						
						if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+strArr[1]);
						try {
							
							couponId = Long.parseLong(strArr[1]);
							
						} catch (NumberFormatException e) {
							
							couponId = null;
						
						}
						
						if(couponId == null) {
							
							//TODO need to delete it from phset or???????????????????????
							continue;
							
						}
						coupon = couponsDao.findById(couponId);
						

						if(coupon == null) {
							
							
							msgStr = msgStr + emailType+ " could not be sent as you have added  Promotion: "+eachPh +" \r\n" ;
							msgStr = msgStr + "This  Promotion is no longer exists, you might have deleted that. : \r\n";
							success = false;
							if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not avalable: "+ eachPh);
							return success;
							
							
						}
						
						
						//only for running && Active coupons  
//						if(!coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING)) {
						if(coupon.getStatus().equals(Constants.COUP_STATUS_EXPIRED) || 
								coupon.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {	
							
							msgStr = msgStr + emailType+" could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
							msgStr = msgStr + "This  Promotion's Status :"+coupon.getStatus()+" and  valid period :"+ 
							MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
							MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
							success = false;
							if(logger.isWarnEnabled()) logger.warn(coupon.getCouponName() + "  Promotion is not in running state, Status : "+ coupon.getStatus());
							return success;

							
							
						}//if
						
						
						if( coupon.getAutoIncrCheck() == true ) {
							continue;
						}
						else if(coupon.getAutoIncrCheck() == false) {
							//need to decide only when auto is false
							//List<Integer> couponCodesList = couponCodesDao.getInventoryCCCountByCouponId(couponId);
							long couponCodeCount = couponCodesDao.getCouponCodeCountByStatus(couponId, Constants.COUP_CODE_STATUS_INVENTORY);
							if(couponCodeCount < 1 ) {
								
								
								
								msgStr = msgStr + emailType+" could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
								msgStr = msgStr + "Available  Promo-codes you can send :"+couponCodeCount+" \r\n";
								success = false;
								if(logger.isWarnEnabled()) logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
								return false;
							}
						
						}//else 
					
				}//for
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::::" , e);
					success = false;
					return false;
					
				}finally{
					
					//send message,set a status as 'paused'
					if(msgStr != null && !msgStr.isEmpty()) {
						
						Messages msgs = new Messages("Send Auto Email", "Auto Email send failed ",
								msgStr, Calendar.getInstance(), "Inbox", false, "Info", user);
						
						//messagesDao.saveOrUpdate(msgs);
						messagesDaoForDML.saveOrUpdate(msgs);
						
						
						
					}//if
					
					if(!success) {
						
						return success;
					}//if
					
				}
			
				
				
				return true;
		
		//************************END OF COUPON CREDITS CHECKING**************
		
		
		
		
	}
	/*private String replaceCouponPhWithBarcode_new(String htmlContent, Users user){
		try{
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
			String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
			String wPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
			String hPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
			
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlContent);
			
			
			while(matcher.find()) {

				String imgtag = null;
				String srcAttr = null;
				String idAttr = null;
				String wAttr = null;
				String hAttr = null;
				
					imgtag = matcher.group();
					
					Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
					Matcher srcm = srcp.matcher(imgtag);
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
					Matcher widthm = widthp.matcher(imgtag);
					
					Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
					Matcher heightm = heightp.matcher(imgtag);
					
					
					while(srcm.find()){
						srcAttr = srcm.group(1);
						logger.debug("srcAttr= "+srcAttr);
					}
					while(idm.find()){
						idAttr = idm.group(1);
						logger.debug("idAttr= "+idAttr);
					}
					while(widthm.find()){
						wAttr = widthm.group(1);
						logger.debug("wAttr= "+wAttr);
					}
					while(heightm.find()){
						hAttr = heightm.group(1);
						logger.debug("hAttr= "+hAttr);
					}
					
					String ccPhTokens[] = idAttr.split("_");

			
			
				
		int width1 = Integer.parseInt(wAttr);
		int height1 = Integer.parseInt(hAttr);
		
		String barcodeType = ccPhTokens[3];
		BitMatrix bitMatrix = null;
		
		String COUPON_CODE_URL = null;
		String ccPreviewUrl = null;
		
		String message = "Test:"+ccPhTokens[2];
		
		if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
			bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width1, height1,null);
			String bcqrImg = user.getUserName()+File.separator+
					"Preview"+File.separator+"QRCODE"+File.separator+idAttr+".png";
			
			COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
			
			ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
		}
		else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
			
			bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width1, height1);
			String bcazImg = user.getUserName()+File.separator+
					"Preview"+File.separator+"AZTEC"+File.separator+idAttr+".png";
			
			COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
			
			ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
		}
		else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
			
			bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width1, height1,null);
			String bclnImg = user.getUserName()+File.separator+
					"Preview"+File.separator+"LINEAR"+File.separator+idAttr+".png";
			
			COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
			
			ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
		}
		else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
			
			bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width1, height1,null);
			String bcdmImg = user.getUserName()+File.separator+
					"Preview"+File.separator+"DATAMATRIX"+File.separator+idAttr+".png";
			
			COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
			
			ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
		}
		
		if(bitMatrix == null){
			return htmlContent;
		}
		File myTemplateFile = new File(COUPON_CODE_URL);
		File parentDir = myTemplateFile.getParentFile();
		 if(!parentDir.exists()) {
				
				parentDir.mkdir();
			}

		
		if(!myTemplateFile.exists()) {
			
			MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
            		new File(COUPON_CODE_URL)));	
		}
		
		logger.debug("htmlcontent= "+htmlContent);
		logger.debug("htmlcontent= "+ccPreviewUrl);
		logger.debug("srcAttr = "+srcAttr);
		htmlContent = htmlContent.replace(srcAttr, ccPreviewUrl);
		
		}
	
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return htmlContent;
		}
		
		return htmlContent;
	}*/
}
/*	private Message buildMessage(String fromEmailId, String htmlContent, String textContent) {
		
		try {
			String boundary = "boundaryTagForAlternative";
			
			
			if(textContent == null) {
				textContent = "No Text version";
			}
				
			 String content = "From: [*from]\n" +
	            "To: [*to]\n" +
	            "Date: [*date]\n" +
	            "Subject: [subject]\n"+
	            "MIME-Version: 1.0\n" +
	            "Content-Type: multipart/alternative; boundary=\"" + boundary + "\"\n" +
	            "\n" +
	            "Content-Type: text/plain; charset=us-ascii\n" +
	            "Content-Transfer-Encoding: 7bit\n" +
	            "\n" +textContent + "\n" +
	            "--" + boundary + "\n" +
	            "Content-Type: text/html; charset=iso-8859-1\n" +
	            "Content-Transfer-Encoding: 8bit\n" +
	            "\n" +htmlContent+"\n" +
	            "--" + boundary + "\n";
			 
			 
			Message msg = new Message(fromEmailId);
			msg.setVirtualMta(vmta);
			msg.addMergeData(content.getBytes());
			
			return msg;
			
		} catch (EmailAddressException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}*/
