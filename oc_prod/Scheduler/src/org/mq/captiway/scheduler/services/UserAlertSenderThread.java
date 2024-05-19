package org.mq.captiway.scheduler.services;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Provider.Type;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.UserCampaignExpiration;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.UserCampaignExpirationDao;
import org.mq.captiway.scheduler.dao.UserCampaignExpirationDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;



public class UserAlertSenderThread extends Thread {
	
	private UserCampaignExpiration userAlertEmailObj;
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private final String DOUBLE_LINE  = "--";
	public UserAlertSenderThread(UserCampaignExpiration userAlertEmailObj) {
		
		this.userAlertEmailObj = userAlertEmailObj;
		
		
	}
	
	public UserAlertSenderThread() {}
	@Override
	public void run() {
		String status = userAlertEmailObj.getStatus();
		try {
			logger.debug(" ===IN UserAlertSenderThread=== ");
			ServiceLocator locator = ServiceLocator.getInstance();
			
			CampaignsDao campaignsDao = (CampaignsDao)locator.getDAOByName(OCConstants.CAMPAIGNS_DAO);
			Campaigns campaign = campaignsDao.findByCampaignId(userAlertEmailObj.getCampaignId());
			
			if(campaign == null) {
				return;
			}
			
			if(!campaign.getUsers().isEnabled()) {
				
				return;
			}
			String toEmailId = campaign.getUsers().getCampExpEmailId();
			if(toEmailId == null) return;
				
			CampaignReportDao campReportDao = (CampaignReportDao)locator.getDAOByName(OCConstants.CAMPAIGN_REPORT_DAO);
			
			CampaignReport campReport = campReportDao.findBy(userAlertEmailObj.getUserId(), campaign.getCampaignName(), false);
			CampaignReport firstCampReport = campReportDao.findBy(userAlertEmailObj.getUserId(), campaign.getCampaignName(), true);
			
			/*if(campReport == null){
				
				return;
			}*/
			
			
			
			
			String subject = PropertyUtil.getPropertyValueFromDB(Constants.SUBJECT_USERCAMPAIGNEXPIRATION);
			String msg = PropertyUtil.getPropertyValueFromDB(OCConstants.MSG_CAMP_EXPIRATION);
			String from = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
			
			String messageHeader =  "{\"unique_args\": {\"Email\": \""+ userAlertEmailObj.getTypeOfAlert() +
					"\", \"userId\": \""+ userAlertEmailObj.getUserId() +"\" , \"ServerName\": \""+ Constants.PROPS_KEY_SERVERNAME +"\" }}";
			
			String msgToBeSent = msg.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_USERNAME, campaign.getUsers().getFirstName())
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_CAMPNAME, campaign.getCampaignName())
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_STARTDATE, MyCalendar.calendarToString(userAlertEmailObj.getStartDate(), MyCalendar.FORMAT_DATEONLY_WITH_DELIMETER))
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_ENDDATE,  MyCalendar.calendarToString(userAlertEmailObj.getEndDate(), MyCalendar.FORMAT_DATEONLY_WITH_DELIMETER))
					//.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_FREEQUENCY, userAlertEmailObj.getFreequency())
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_LASTSENTON, MyCalendar.calendarToString(campReport!= null ? campReport.getSentDate() : null, MyCalendar.FORMAT_DATEONLY_WITH_DELIMETER))
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_SENTCOUNT, campReport!= null ? (campReport.getSent()+Constants.STRING_NILL) : DOUBLE_LINE)
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_UNDELIVERED,  campReport!= null ? (campReport.getBounces()+Constants.STRING_NILL) : DOUBLE_LINE)
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_OPENS, campReport!= null ? (campReport.getOpens()+Constants.STRING_NILL) : DOUBLE_LINE)
					.replace(OCConstants.CAMP_EXPIRATION_EMAIL_PH_CLICKS, campReport!= null ? (campReport.getClicks()+Constants.STRING_NILL) : DOUBLE_LINE);
			
			logger.debug(" ===before submitSimpleEmail=== ");
			
			userAlertEmailObj.setToEmailId(toEmailId);
			userAlertEmailObj.setLastSentOn(Calendar.getInstance());
			userAlertEmailObj.setMsgContent(msgToBeSent);
			
			UserCampaignExpirationDao userCampaignExpirationDao = (UserCampaignExpirationDao)locator.
					getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
			
			UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)locator.
					getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);

			
			//userCampaignExpirationDao.saveOrUpdate(userAlertEmailObj);
			userCampaignExpirationDaoForDML.saveOrUpdate(userAlertEmailObj);

			
			submitSimpleEmail(messageHeader, msgToBeSent, msgToBeSent, from, subject, toEmailId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Expetion ", e);
		}finally{
			
			
		}
		
	}
	
	public void submitSimpleEmail(String messageHeader,String htmlContent,
		String textContent, String fromField, String subject, 
		String toField) {
		logger.debug(" ===in submitSimpleEmail=== "+subject+ " "+htmlContent+" "+fromField+" "+toField);
		try {
			Properties props = new Properties();
			/* props.put("mail.transport.protocol", "smtp");
			 props.put("mail.smtp.host", "smtp.sendgrid.net");
			 props.put("mail.smtp.port", 587);
			 props.put("mail.smtp.auth", "true");*/
			 //Added for 2.3.12
			 props.put(Constants.SMTP_KEY_PROTOCOL, Constants.SMTP_VALUE_PROTOCOL);
			 props.put(Constants.SMTP_KEY_HOST, Constants.SMTP_VALUE_HOST);
			 props.put(Constants.SMTP_KEY_PORT, Integer.parseInt(Constants.SMTP_VALUE_PORT));
			 props.put(Constants.SMTP_KEY_AUTH, Constants.SMTP_VALUE_AUTH);
			 
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
		    
		    message.setFrom(new InternetAddress("alerts@optculture.com"));
		    Address intAddress[] = new InternetAddress[1];

			intAddress[0] =  new InternetAddress(toField);
		   /* if(replyTo != null)
		    {
			intAddress[0] =  new InternetAddress(replyTo);
			message.setReplyTo( intAddress );
		    }*/
		    
		    message.setSubject(subject);
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toField));
		    
		    // Added for CC Email Id
		    
		    /*if(ccEmailId != null){
		    	
		    	message.addRecipient(Message.RecipientType.CC,new InternetAddress(ccEmailId));
		    	
		    }*/
		    
		    
	        transport.connect();
	        transport.sendMessage(message,message.getRecipients(Message.RecipientType.TO));
	        transport.close();
	        logger.debug(" ===exit submitSimpleEmail=== ");
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	}


	public class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			// TODO Auto-generated method stub
			String SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
			String SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);

	         return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
		}
    
}
}
