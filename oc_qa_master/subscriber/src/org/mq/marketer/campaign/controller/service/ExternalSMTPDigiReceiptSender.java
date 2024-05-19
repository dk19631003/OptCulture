package  org.mq.marketer.campaign.controller.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Provider;
import javax.mail.Provider.Type;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.springframework.context.ApplicationContext;

public class ExternalSMTPDigiReceiptSender {
	
	// public static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	 //public static String SMTP_AUTH_USER;
	// public static String SMTP_AUTH_PWD;

	 public static String SMTP_SINGLE_AUTH_USER;
	 public static String SMTP_SINGLE_AUTH_PWD;

	 public static Properties props;
	 
	 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	

	 
	 
	public ExternalSMTPDigiReceiptSender() {
		
		try {
			
			//SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
		//	SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);

			SMTP_SINGLE_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID);
			SMTP_SINGLE_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD);
			

			/* props = new Properties();
			 props.put("mail.transport.protocol", "smtp");
			 props.put("mail.smtp.host", SMTP_HOST_NAME);
			 props.put("mail.smtp.port", 587);
			 props.put("mail.smtp.auth", "true");*/
			
			//Added in 2.3.11
			 props = new Properties();
			 props.put(Constants.SMTP_KEY_PROTOCOL, Constants.SMTP_VALUE_PROTOCOL);
			 props.put(Constants.SMTP_KEY_HOST, Constants.SMTP_VALUE_HOST);
			 props.put(Constants.SMTP_KEY_PORT, 465);//Integer.parseInt(Constants.SMTP_VALUE_PORT));
			 props.put(Constants.SMTP_KEY_AUTH, Constants.SMTP_VALUE_AUTH);
			 props.put("mail.smtp.socketFactory.port", "465");
			 props.put("mail.smtp.socketFactory.class",  "javax.net.ssl.SSLSocketFactory");
			 props.put("mail.smtp.ssl.protocols","TLSv1.2");  // handshake exception

			
			 
		} catch (Exception e) {
			logger.error("** Error Property is not defined : "+Constants.PROPS_KEY_SENDGRID_THREAD_COUNT);
		}
		
	}
	
	
	public void submitDigitalreceipt(String messageHeader,String htmlContent,String textContent, String fromField, String subject,String toField) {
		
		try {
							
		        Authenticator auth = new  SMTPAuthenticator();
		        
		        Session mailSession = Session.getInstance(props, auth);
		    
		        // uncomment for debugging infos to stdout
		        //mailSession.setDebug(true);
		        Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
		 
		        MimeMessage message = new MimeMessage(mailSession);
		 
		        Multipart multipart = new MimeMultipart("alternative");
		 
		        BodyPart part1 = new MimeBodyPart();
		        part1.setText(textContent);
		 
		        BodyPart part2 = new MimeBodyPart();
		        part2.setContent(htmlContent, "text/html; charset=utf-8");
		 
		        multipart.addBodyPart(part1);
		        multipart.addBodyPart(part2);
		 
		        message.addHeader("X-SMTPAPI", messageHeader);
			        
			    message.setContent(multipart);
			    
			    message.setFrom(new InternetAddress(fromField));
			    message.setSubject(subject);
			    message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField));
		        
		        transport.connect();
		        transport.sendMessage(message,message.getRecipients(Message.RecipientType.TO));
		        transport.close();
		        
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	
	}//submitDigitalreceipt()
	
	
	public void submitDigitalreceiptWithReplyTo(String messageHeader,String htmlContent,String textContent, String fromField, String subject,String toField, String replyToField) {
		
		try {
							
		        Authenticator auth = new  SMTPAuthenticator();
		        
		        Session mailSession = Session.getInstance(props, auth);
		    
		        // uncomment for debugging infos to stdout
		        //mailSession.setDebug(true);
		        Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
		 
		        MimeMessage message = new MimeMessage(mailSession);
		 
		        Multipart multipart = new MimeMultipart("alternative");
		 
		        BodyPart part1 = new MimeBodyPart();
		        part1.setText(textContent);
		 
		        BodyPart part2 = new MimeBodyPart();
		        part2.setContent(htmlContent, "text/html; charset=utf-8");
		 
		        multipart.addBodyPart(part1);
		        multipart.addBodyPart(part2);
		 
		        message.addHeader("X-SMTPAPI", messageHeader);
			        
			    message.setContent(multipart);
			    
			    message.setFrom(new InternetAddress(fromField));
			    message.setSubject(subject);
			    message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField));
			    //
			    InternetAddress[] arrayOfReplyTo = new InternetAddress[1];
			    arrayOfReplyTo[0] = new InternetAddress(replyToField);
			    message.setReplyTo(arrayOfReplyTo);
		        //
			    
		        transport.connect();
		        transport.sendMessage(message,message.getRecipients(Message.RecipientType.TO));
		        transport.close();
		        
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	
	}//submitDigitalreceiptWithReplyTo()
	
  
	public class SMTPAuthenticator extends javax.mail.Authenticator {
	      public PasswordAuthentication getPasswordAuthentication() {
	           return new PasswordAuthentication(SMTP_SINGLE_AUTH_USER, SMTP_SINGLE_AUTH_PWD);
	      }
	 }
	
	

}
