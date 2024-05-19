package org.mq.marketer.campaign.controller.service;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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

public class ExternalSMTPSupportSender {

	
	 public  final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	 public static String SMTP_AUTH_USER;
	 public static String SMTP_AUTH_PWD;

	 public static String SMTP_SINGLE_AUTH_USER;
	 public static String SMTP_SINGLE_AUTH_PWD;

	 public static Properties props;
	 
	 private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	

	 
	 
	public  ExternalSMTPSupportSender() {
		
		try {
			
			SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
			SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);

			SMTP_SINGLE_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID);
			SMTP_SINGLE_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD);
			

			 /*props = new Properties();
			 props.put("mail.transport.protocol", "smtp");
			 props.put("mail.smtp.host", SMTP_HOST_NAME);
			 props.put("mail.smtp.port", 587);
			 props.put("mail.smtp.auth", "true");*/
			//Added in 2.3.11
			 props = new Properties();
			 props.put(Constants.SMTP_KEY_PROTOCOL, Constants.SMTP_VALUE_PROTOCOL);
			 props.put(Constants.SMTP_KEY_HOST, Constants.SMTP_VALUE_HOST);
			 props.put(Constants.SMTP_KEY_PORT, 465);// props.put(Constants.SMTP_KEY_PORT, Integer.parseInt(Constants.SMTP_VALUE_PORT));
			 props.put(Constants.SMTP_KEY_AUTH, Constants.SMTP_VALUE_AUTH);
			 props.put("mail.smtp.socketFactory.port", "465");
			 props.put("mail.smtp.socketFactory.class",  "javax.net.ssl.SSLSocketFactory");
			 props.put("mail.smtp.ssl.protocols","TLSv1.2"); //handshake exception

			 
			 
		} catch (Exception e) {
			logger.error("** Error Property is not defined : "+Constants.PROPS_KEY_SENDGRID_THREAD_COUNT);
		}
		
	}
	
	
	public void submitSupportMail(String messageHeader,String htmlContent,String textContent, String fromField, 
																String subject,String toField,String fileName ,String filePath) {
		
		try {
			submitGeneralMail(messageHeader, htmlContent, textContent, fromField, subject, new String[]{toField},new String[]{}, new String[]{}, fileName, filePath);
		        
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	
	}//submitDigitalreceipt()
	public void submitSupportAckMail(String messageHeader,String htmlContent,String textContent, String fromField, 
										String subject,String toField) {

			try {
			
			Authenticator auth = new  SMTPAuthenticator();
			
			Session mailSession = Session.getInstance(props, auth);
			
			// uncomment for debugging infos to stdout
			//mailSession.setDebug(true);
			Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
			
			MimeMessage message = new MimeMessage(mailSession);
			
			Multipart multipart = new MimeMultipart("alternative");
			/*
			// Added for file attachment 
			MimeBodyPart attachmentPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath + fileName);
			attachmentPart.setDataHandler(new DataHandler(source));
			
			attachmentPart.setFileName(fileName);
			multipart.addBodyPart(attachmentPart);*/
			
			BodyPart part1 = new MimeBodyPart();
			part1.setText(textContent);
			
			BodyPart part2 = new MimeBodyPart();
			part2.setContent(htmlContent, "text/html");
			
			multipart.addBodyPart(part1);
			multipart.addBodyPart(part2);
			
			message.addHeader("X-SMTPAPI", messageHeader);
			
			message.setContent(multipart);
			
			message.setFrom(new InternetAddress(fromField));
			message.setSubject(subject);
			
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField));
			
	        
	        transport.connect();
	        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	        transport.close();
	        
			
			} catch(Exception e) {
			logger.error("** Exception : ",e);
			} 

	}//submitDigitalreceipt()
	
	
	public void submitGeneralMail(String messageHeader,String htmlContent,String textContent, String fromEmail,
			String subject,String[] toEmail, String[] ccEmail, String[] bccEmail){
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
			part2.setContent(htmlContent, "text/html");

			multipart.addBodyPart(part1);
			multipart.addBodyPart(part2);

			message.addHeader("X-SMTPAPI", messageHeader);

			message.setContent(multipart);

			message.setFrom(new InternetAddress(fromEmail));
			message.setSubject(subject);
			InternetAddress[] address = null;
			// added for multiple recipients
			if(toEmail != null)
			{
			address = new InternetAddress[toEmail.length];
			for(int i =0; i< toEmail.length; i++)
			{
				address[i] = new InternetAddress(toEmail[i]);
			}
			message.addRecipients(Message.RecipientType.TO,address);
			}
			
			if(ccEmail != null)
			{
			address = new InternetAddress[ccEmail.length];
			for(int i =0; i< ccEmail.length; i++)
			{
				address[i] = new InternetAddress(ccEmail[i]);
			}
			message.addRecipients(Message.RecipientType.CC,address);
			}
			
			if(bccEmail != null)
			{
			address = new InternetAddress[bccEmail.length];
			for(int i =0; i< bccEmail.length; i++)
			{
				address[i] = new InternetAddress(bccEmail[i]);
			}
			message.addRecipients(Message.RecipientType.BCC,address);
			}
			transport.connect();
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();

		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
		
	}//submitGeneralMail
	
	public void submitGeneralMail(String messageHeader,String htmlContent,String textContent, String fromEmail,
			String subject,String[] toEmail,String[] ccEmail,String[] bccEmail,String fileName ,String filePath){
		try {

			Authenticator auth = new  SMTPAuthenticator();

			Session mailSession = Session.getInstance(props, auth);

			// uncomment for debugging infos to stdout
			//mailSession.setDebug(true);
			Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
							
			MimeMessage message = new MimeMessage(mailSession);
			Multipart mainMultipart = new MimeMultipart("mixed");
			
			MimeMultipart multipart = new MimeMultipart("alternative");

			BodyPart part1 = new MimeBodyPart();
			part1.setText(textContent);

			BodyPart part2 = new MimeBodyPart();
			part2.setContent(htmlContent, "text/html");

			multipart.addBodyPart(part1);
			multipart.addBodyPart(part2);

			message.addHeader("X-SMTPAPI", messageHeader);
			
			MimeBodyPart htmlAndTextBodyPart = new MimeBodyPart();
	        htmlAndTextBodyPart.setContent(multipart);
	        mainMultipart.addBodyPart(htmlAndTextBodyPart);
	        
	        if(fileName != null && filePath != null){
		        MimeBodyPart filePart = new MimeBodyPart();
		        	
		        FileDataSource fds = new FileDataSource(filePath);
		        filePart.setDataHandler(new DataHandler(fds));
		        filePart.setFileName(fileName);
		        mainMultipart.addBodyPart(filePart);
		        
	        }
	               
	        message.setContent(mainMultipart);
			message.setFrom(new InternetAddress(fromEmail));
			message.setSubject(subject);
			
			// added for multiple recipients
			InternetAddress[] address = null;
			if(toEmail != null)
			{
			address = new InternetAddress[toEmail.length];
			for(int i =0; i< toEmail.length; i++)
			{
				address[i] = new InternetAddress(toEmail[i]);
			}
			message.addRecipients(Message.RecipientType.TO,address);
			}
			
			if(ccEmail != null)
			{
			address = new InternetAddress[ccEmail.length];
			for(int i =0; i< ccEmail.length; i++)
			{
				address[i] = new InternetAddress(ccEmail[i]);
			}
			message.addRecipients(Message.RecipientType.CC,address);
			}
			
			if(bccEmail != null)
			{
			address = new InternetAddress[bccEmail.length];
			for(int i =0; i< bccEmail.length; i++)
			{
				address[i] = new InternetAddress(bccEmail[i]);
			}
			message.addRecipients(Message.RecipientType.BCC,address);
			}
			transport.connect();
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();
			if(new File(filePath).delete()){
	        	logger.info(fileName+" is deleted ");
	        }else {
	        	logger.info(fileName+" is not deleted ");
	        }

		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	}//submitGeneralMail
	
	public void submitOptSyncMail(String messageHeader,String htmlContent,String textContent, String fromField, 
			String subject,String[] toField){
		try {

			logger.info("enterd in opt sync");
			Authenticator auth = new  SMTPAuthenticator();
			
			Session mailSession = Session.getInstance(props, auth);
			
			// uncomment for debugging infos to stdout
			//mailSession.setDebug(true);
			Transport transport = mailSession.getTransport(new Provider(Type.TRANSPORT,"smtp","com.sun.mail.smtp.SMTPTransport","Sun Microsystems","Inc"));
			
			MimeMessage message = new MimeMessage(mailSession);
			
			Multipart multipart = new MimeMultipart("alternative");
			/*
			// Added for file attachment 
			MimeBodyPart attachmentPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath + fileName);
			attachmentPart.setDataHandler(new DataHandler(source));
			
			attachmentPart.setFileName(fileName);
			multipart.addBodyPart(attachmentPart);*/
			
			BodyPart part1 = new MimeBodyPart();
			part1.setText(textContent);
			
			BodyPart part2 = new MimeBodyPart();
			part2.setContent(htmlContent, "text/html");
			
			multipart.addBodyPart(part1);
			multipart.addBodyPart(part2);
			
			message.addHeader("X-SMTPAPI", messageHeader);
			
			message.setContent(multipart);
			
			message.setFrom(new InternetAddress(fromField));
			message.setSubject(subject);
			
			// added for multiple recipients

			InternetAddress[] address = new InternetAddress[toField.length];
			for(int i =0; i< toField.length; i++)
			{
				address[i] = new InternetAddress(toField[i]);
			}
			message.addRecipients(Message.RecipientType.TO,address);
			
			//message.addRecipient(Message.RecipientType.TO,new InternetAddress(toField));
			
	        
	        transport.connect();
	        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	        transport.close();
		} catch(Exception e) {
			logger.error("** Exception : ",e);
		} 
	}//submitOptSyncMail
	
	public class SMTPAuthenticator extends javax.mail.Authenticator {
	      public PasswordAuthentication getPasswordAuthentication() {
	           return new PasswordAuthentication(SMTP_SINGLE_AUTH_USER, SMTP_SINGLE_AUTH_PWD);
	      }
	 }
	
	



}
