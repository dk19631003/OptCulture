package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.FarwardToFriend;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.FarwardToFriendDao;
import org.mq.captiway.scheduler.dao.FarwardToFriendDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.EncryptDecryptUrlParameters;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.springframework.context.ApplicationContext;

public class FarwardToFriendSender extends TimerTask  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private static final String FARWARD_MSG =" has forwarded you this email with the following message:";
	
	private static final String ERROR_RESPONSE = 
			"<div style='font-size:15px;color:blue;font-family:verdena;" +
			"font-weight:bold;margin-top:50px'>The Web Page is expired</div>";
	//private StringBuffer messageHeader = new StringBuffer(Constants.SENDGRID_HEADER_JSON);
	
	
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	public UsersDaoForDML getUsersDaoForDML() {
		return usersDaoForDML;
	}
	public void setUsersDaoForDML(UsersDaoForDML usersDaoForDML) {
		this.usersDaoForDML = usersDaoForDML;
	}
	private CampaignSentDao campaignSentDao;
	private CampaignReportDao campaignReportDao;
	private FarwardToFriendDao farwardToFriendDao;
	private FarwardToFriendDaoForDML farwardToFriendDaoForDML;

	public FarwardToFriendDaoForDML getFarwardToFriendDaoForDML() {
		return farwardToFriendDaoForDML;
	}
	public void setFarwardToFriendDaoForDML(
			FarwardToFriendDaoForDML farwardToFriendDaoForDML) {
		this.farwardToFriendDaoForDML = farwardToFriendDaoForDML;
	}
	private ContactsDao contactsDao;
	private volatile boolean isRunning;
	private CampaignsDao campaignsDao;
	
	
	public CampaignsDao getCampaignsDao() {
		return campaignsDao;
	}
	public void setCampaignsDao(CampaignsDao campaignsDao) {
		this.campaignsDao = campaignsDao;
	}
	public UsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	public CampaignSentDao getCampaignSentDao() {
		return campaignSentDao;
	}
	public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
		this.campaignSentDao = campaignSentDao;
	}
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	public FarwardToFriendDao getFarwardToFriendDao() {
		return farwardToFriendDao;
	}
	public void setFarwardToFriendDao(FarwardToFriendDao farwardToFriendDao) {
		this.farwardToFriendDao = farwardToFriendDao;
	}
	
	
	public ContactsDao getContactsDao() {
		return contactsDao;
	}
	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	ApplicationContext context;
	
	
	
	
	
	// public static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	 public static String SMTP_AUTH_USER;
	 public  static String SMTP_AUTH_PWD;

	 /*public  String SMTP_SINGLE_AUTH_USER;
	 public  String SMTP_SINGLE_AUTH_PWD;*/
	 public  Properties props;
	 private final String DIV_TEMPLATE = PropertyUtil.getPropertyValue("divTemplate");
	 
	/*private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	static {
		
		genFieldContMap.put("email", "EmailId");
		genFieldContMap.put("firstName", "FirstName");
		genFieldContMap.put("lastName", "LastName");
		genFieldContMap.put("addressOne", "AddressOne");
		genFieldContMap.put("addressTwo", "AddressTwo");
		genFieldContMap.put("city", "City");
		genFieldContMap.put("state", "State");
		genFieldContMap.put("country", "Country");
		genFieldContMap.put("pin", "Zip");
		genFieldContMap.put("phone", "MobilePhone");
		genFieldContMap.put("gender", "Gender");
		genFieldContMap.put("birthday", "BirthDay");
		genFieldContMap.put("anniversary", "Anniversary");
		genFieldContMap.put("zip", "Zip");
		genFieldContMap.put("mobile", "MobilePhone");
		
	}*/
 private  final String serverName = Constants.PROPS_KEY_SERVERNAME;
	 
	/* static {

		 serverName = PropertyUtil.getPropertyValue(Constants.PROPS_KEY_APPLICATION_IP);
			
	 }*/

	
	 public FarwardToFriendSender() {
			try {
				
				SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
				SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);

			/*	SMTP_SINGLE_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID);
				SMTP_SINGLE_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD);
			*/	

				 props = new Properties();
				 props.put(Constants.SMTP_KEY_PROTOCOL, Constants.SMTP_VALUE_PROTOCOL);
				 props.put(Constants.SMTP_KEY_HOST, Constants.SMTP_VALUE_HOST);
				 props.put(Constants.SMTP_KEY_PORT, Integer.parseInt(Constants.SMTP_VALUE_PORT));
				 props.put(Constants.SMTP_KEY_AUTH, Constants.SMTP_VALUE_AUTH);
				 
				 
				 
			} catch (Exception e) {
				logger.error("** Error Property is not defined : "+Constants.PROPS_KEY_SENDGRID_THREAD_COUNT);
			}
			
		}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(logger.isInfoEnabled()) logger.info("------- just entered farward  to push events to DB -------");
		isRunning = true;
		List<FarwardToFriend> farList = farwardToFriendDao.findByStatus(Constants.EQ_STATUS_ACTIVE);
		if(farList.size() <= 0)
			return;
		
		Users user= null;
		
		JSONObject SendGridHeader = null ;
		JSONArray  toEmailArr = null;
		JSONObject toEmail = null;
		String htmlStr = null;
		Long sentId = null;
		List<FarwardToFriend> forwardUpdateList = new ArrayList<FarwardToFriend>();
		String emailId = null;
		String subject = null;
		String emailStr= null;//can be deleted?????
		String refererStr = null;
		String fromStr = null;
		Long userId=null;
		
		for (FarwardToFriend farwardToFriend : farList) {
			
    		//params required in preparing msg header
			emailId = farwardToFriend.getToEmailId();
			 emailStr= farwardToFriend.getEmail();
			 refererStr=farwardToFriend.getReferer();
			 userId = farwardToFriend.getUserId();
			 user = usersDao.findByUserId(userId);
			if(sentId == null){
				
				String[] retArr = prepareHtmlContent(farwardToFriend);
				if(retArr == null) {
					
					
					return;
				}
				
				htmlStr = retArr[0];
				subject = retArr[1];
				emailStr = retArr[2];
				
				 SendGridHeader = new JSONObject();
				 sentId = farwardToFriend.getSentId().longValue();
				 emailId = farwardToFriend.getToEmailId();
					
					
					toEmailArr = new JSONArray();
					//toEmailArr.add(emailId);
					
					logger.info("toEmailArr  null::"+toEmailArr);
					fromStr = refererStr + "<"+emailStr+">";
					
				
			}//
			
			if(sentId != farwardToFriend.getSentId().longValue()){
				
				
				// sending logic
				
				SendGridHeader.put("to", toEmailArr);
				logger.info("mesageHeader ::"+SendGridHeader.toString());
				
				submitSimpleEmail(SendGridHeader.toString(),htmlStr,fromStr,subject);
				
				// added for user credits
				//usersDao.updateUsedEmailCount(userId, toEmailArr.size());
				usersDaoForDML.updateUsedEmailCount(userId, toEmailArr.size());
				
				//user.setUsedEmailCount(user.getUsedEmailCount()+toEmailArr.size());
				
				
				
				//restore the current iteration of foraward object
				sentId = farwardToFriend.getSentId();
				emailId = farwardToFriend.getToEmailId();
				String[] retArr = prepareHtmlContent(farwardToFriend);
				if(retArr == null) {
					
					
					return;
				}
				
				htmlStr = retArr[0];
				subject = retArr[1];
				emailStr = retArr[2];
				
				 SendGridHeader = new JSONObject();
	    		
					//TODO toEmail.put("to", toEmailArr);
					toEmailArr = new JSONArray();
					logger.info("toEmailArr not null ::"+toEmailArr);
					
				
			
			}
			
			
			toEmailArr.add(emailId);
			
			
			
		
		farwardToFriend.setStatus(Constants.CS_STATUS_SUCCESS);
		forwardUpdateList.add(farwardToFriend);
		if(forwardUpdateList.size() >= 100) {
			
			//farwardToFriendDao.saveByCollection(forwardUpdateList);
			farwardToFriendDaoForDML.saveByCollection(forwardUpdateList);

			forwardUpdateList.clear();
		}
			
			
		}//foreach
		
		if(toEmailArr.size() > 0){
			SendGridHeader.put("to", toEmailArr);
			submitSimpleEmail(SendGridHeader.toString(),htmlStr,fromStr,subject);
			
			//usersDao.updateUsedEmailCount(userId, toEmailArr.size());
			usersDaoForDML.updateUsedEmailCount(userId, toEmailArr.size());
			
			//user.setUsedEmailCount(user.getUsedEmailCount()+toEmailArr.size());
			logger.info("toEmailArr final ::"+toEmailArr);
		}
		
		if(forwardUpdateList.size() > 0) {
			
			//farwardToFriendDao.saveByCollection(forwardUpdateList);
			farwardToFriendDaoForDML.saveByCollection(forwardUpdateList);

			forwardUpdateList.clear();
		}
		
	}//run()
	
	
private String[] prepareHtmlContent(FarwardToFriend farwardToFriend){
		
		try {
			//preparing  html stuff
			
			String custMsg = farwardToFriend.getCustMsg();
			String referName = farwardToFriend.getReferer();
			Long crId= farwardToFriend.getCrId();
			Long sentId = farwardToFriend.getSentId().longValue(); 
			if(sentId == null) {
				if(logger.isWarnEnabled()) logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			if(campaignSent == null) {
				if(logger.isWarnEnabled()) logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			
			CampaignReport camprep= campaignSent.getCampaignReport();
			if(camprep == null) {
				if(logger.isWarnEnabled()) logger.warn(" Campaign Report object not found for sentId :"+sentId);
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			
			
			
			String htmlStr = camprep.getContent();
			if(htmlStr == null) {
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			String  phValStr = campaignSent.getContactPhValStr();
			Long userId = farwardToFriend.getUserId();
			Long contactId = farwardToFriend.getContactId();
			Contacts contact = contactsDao.findById(contactId);
			String subject = camprep.getSubject();
			String emailId = farwardToFriend.getToEmailId();
			
			
			
			// delete unsubscribe link and farward message starting of the html 
			
			String msg = referName +FARWARD_MSG;
			msg =  StringEscapeUtils.escapeHtml(msg);
			custMsg =  StringEscapeUtils.escapeHtml(custMsg);
			
			/*String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth]{width:100% !important; height:auto !important;} *[class=headerblock] {width:100% !important; height:auto !important;}	*[class=headerblock] img{width:100% !important; height:auto !important;}	*[class=prodblock]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock] img{width:100% !important; height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			htmlStr =htmlStr.replace(mobileHeadStr, mobileHeadStr+msg+" <br/> <br/> "+custMsg+" </br>");*/
			String headStr   = "<HTML><HEAD></HEAD><BODY>";
			String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
		//	String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{width:100% !important; height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
			if(htmlStr.startsWith(headStr)){
				htmlStr =htmlStr.replace(headStr, headStr+msg+" <br/> <br/> "+custMsg+" </br> </br>");
			}else{
				
				htmlStr =htmlStr.replace(mobileHeadStr, mobileHeadStr+msg+" <br/> <br/> "+custMsg+" </br> </br>");
			}
			Long campainId =  campaignSent.getCampaignId();
			Campaigns campaign = campaignsDao.findByCampaignId(campainId);
			
			// TODO for the campaigns already sent
			boolean oldCampSentFlag = false;
			if(htmlStr.contains(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL))) {
				htmlStr = htmlStr.replace(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL), PropertyUtil.getPropertyValue(Constants.NEW_TRACK_URL));
				oldCampSentFlag = true;
			}
			String webLinkText = campaign.getWebLinkText();
			String webLinkUrlText = campaign.getWebLinkUrlText();
			
			
			String weblinkUrl =  PropertyUtil.getPropertyValue("weblinkUrl");
			weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
			String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
			
			htmlStr = htmlStr.replace(webUrl, "");
			
			//  for non - subscriber preference center
			
			String unSubUrl = PropertyUtil.getPropertyValue("unSubscribeUrl");
			unSubUrl =unSubUrl.replace("|^", "[").replace("^|", "]");
			if(oldCampSentFlag) {
				unSubUrl = unSubUrl.replace("[userId]", userId.toString());
			}
			else {
				unSubUrl = unSubUrl.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.toString()));
			}
			
			String unsubDiv = PropertyUtil.getPropertyValue("unsubFooterText");
			unsubDiv = unsubDiv.replace("|^unsubUrl^|", unSubUrl);
			
			
			htmlStr =htmlStr.replace(unsubDiv, "");
			
			
			// remove update subscription link  and unsubscribe
			
			String upadteUnsubDiv = PropertyUtil.getPropertyValue("updateUnsubFooterText");
			upadteUnsubDiv = upadteUnsubDiv.replace("|^unsubUrl^|", unSubUrl);
			
			htmlStr = htmlStr.replace(upadteUnsubDiv, "");
			
			String updateSubsLink =  PropertyUtil.getPropertyValue("updateSubscriptionLink");
			updateSubsLink =updateSubsLink.replace("|^", "[").replace("^|", "]");
		
			String updateUnsubDiv = PropertyUtil.getPropertyValue("updateSubHTMLTxt");
			updateUnsubDiv = updateUnsubDiv.replace("|^updateSubSUrl^|", updateSubsLink);
			
			htmlStr = htmlStr.replace(updateUnsubDiv, "");

			
			if(htmlStr.contains("href='")){
				htmlStr = htmlStr.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
				
			}
			if(htmlStr.contains("href=\"")){
				htmlStr = htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
			}
			
			
			//replacement logic for subject symbol place holders
			subject = subject.replace("|^", "[").replace("^|", "]");
			
			Set<String> symbolSet = getSubjectSymbolFields(htmlStr+subject);
			if(symbolSet != null && symbolSet.size()>0){
				
				//logger.debug("symbolSet ==========>"+symbolSet);
				for (String symbol : symbolSet) {
						if(symbol.startsWith(Constants.SYMBOL_PH_SYM)) {
							subject = subject.replace("["+symbol+"]", PropertyUtil.getPropertyValueFromDB(symbol));
						
						}
						else if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
							if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
								Calendar cal = MyCalendar.getNewCalendar();
								subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
								subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
								
								try {
									String[] days = symbol.split("_");
									Calendar cal = MyCalendar.getNewCalendar();
									cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
									subject = subject.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
								} catch (Exception e) {
									logger.debug("exception in parsing date placeholder");
								}
							}
						}
				}
			}
			//contact home store and last purchase place holders replacement logic
			String placeHoldersStr = camprep.getPlaceHoldersStr();
			if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
				String contactPhValStr = campaignSent.getContactPhValStr();
				if(contactPhValStr != null) {
					
					String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
					String keyStr = "";
					String ValStr = "";
					for (String phToken : phTokenArr) {
						keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
						if(!( keyStr.equalsIgnoreCase("[GEN_ContactHomeStore]") ) && ! (keyStr.equalsIgnoreCase("[GEN_ContactLastPurchasedStore]")) &&  !( keyStr.startsWith("[CC_")) ){
							continue;
						}
						ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());
						
						htmlStr = htmlStr.replace(keyStr, ValStr);
						
						
					
						
					}	
			
				}
			}
			htmlStr = htmlStr.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlStr = htmlStr.replace("[cId]", EncryptDecryptUrlParameters.encrypt(contactId.toString()));
			htmlStr = htmlStr.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.longValue()+""));
			htmlStr = htmlStr.replace("[email]",  campaignSent.getEmailId());
			String[] retArr = new String[3];
			
			retArr[0] = htmlStr;
			retArr[1] = subject;
			retArr[2] = campaignSent.getEmailId();
			
			
			return retArr;
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		} 
		
	}
	
		private Set<String> getSubjectSymbolFields(String content) {
			
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
					}else if(ph.startsWith(Constants.DATE_PH_DATE_)){
						subjectSymbolSet.add(ph);
					}else if(ph.startsWith("CC_")){
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
	
	
	
private void submitSimpleEmail(String messageHeader,String inHtmlContent, String fromField,  String subject) {
		
	try {
		Authenticator auth = new SMTPAuthenticator();
		
		Session mailSession = Session.getInstance(props, auth);

		// uncomment for debugging infos to stdout
		//mailSession.setDebug(true);
		Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
		transport.connect();

		MimeMessage message = new MimeMessage(mailSession);

		Multipart multipart = new MimeMultipart("alternative");

   

		BodyPart part = new MimeBodyPart();
		part.setContent(inHtmlContent, "text/html");

		multipart.addBodyPart(part);

		message.addHeader("X-SMTPAPI", messageHeader);
		    
		message.setContent(multipart);
		
		message.setFrom(new InternetAddress(fromField));
		message.setSubject(subject);
		
		message.addHeader("X-SMTPAPI", messageHeader);
		
		//logger.info(")))))))>>> "+messageHeader);
		message.setContent(multipart);
		
		message.setFrom(new InternetAddress(fromField));
		message.setSubject(subject);
		message.addRecipient(Message.RecipientType.TO,  new InternetAddress(Constants.SMTP_RECIEPIENT_EMAIL_ID));
		
		
		
		transport.send(message);
		
	} catch (AddressException e) {
		logger.error("Address Exception ::::" , e);
	} catch (NoSuchProviderException e) {
		logger.error("NoSuchProviderException::::" , e);
	} catch (MessagingException e) {
		logger.error("MessagingException ::::" , e);
	}
    


	}
	
	
	 public class SMTPAuthenticator extends javax.mail.Authenticator {
	      public PasswordAuthentication getPasswordAuthentication() {
	           return new PasswordAuthentication(FarwardToFriendSender.SMTP_AUTH_USER, FarwardToFriendSender.SMTP_AUTH_PWD);
	      }
	 }
	
	

}
