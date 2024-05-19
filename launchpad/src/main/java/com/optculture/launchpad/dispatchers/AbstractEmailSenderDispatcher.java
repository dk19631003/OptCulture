package com.optculture.launchpad.dispatchers;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.optculture.shared.entities.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optculture.launchpad.configs.OCConstants;
import com.optculture.launchpad.dto.CommunicationJSONDump;
import com.optculture.launchpad.services.CommunicationService;
import com.optculture.shared.entities.contact.Contact;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Provider;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/*
 * This class serve common point for all email senders.
 */
@Qualifier("AbstractEmailSenderDispatcher")
@Component("AbstractEmailSenderDispatcher")
public abstract class AbstractEmailSenderDispatcher implements ChannelDispatcher {

	Logger logger = LoggerFactory.getLogger(AbstractEmailSenderDispatcher.class);
	String fromEmail = "";// "sameera@optculture.com";
	String toEmail = "";// "sameera@optculture.com";
	String subject = ""; // "Welcome to launchPad";
	String replyToEmail = "";// "sameera@maildrop.cc";
	String fromName = "";// "Welcome";
	String replyToName = "";

	@Value(value = ("${textMsg}"))
	String textMsg;

	private ObjectMapper mapper = new ObjectMapper();

	// As these are needed in child class.
	protected boolean isConnected = false;

	protected Transport transport;

	@Override
	@Retryable(retryFor = { Exception.class }, maxAttempts = 10, backoff = @Backoff(delay = 1000))
	public void dispatch(String finalContent, Contact contactobj, ChannelAccount channelAccount,
                         ChannelSetting channelSetting, UserChannelSetting userChannelSettingObj, Communication commObj,
                         CustomCommunication in) throws Exception {
		CommunicationJSONDump jsonDump = null;
		try {

			logger.info("Sending email over " + channelSetting.getChannelType() + "to" + contactobj.getEmailId());
			CommunicationService commService = new CommunicationService(mapper);
			String dump = commObj.getAttributes();
			logger.debug("Dump object in the communication " + dump);

			jsonDump = commService.retrieveCommunicationJSONDumpById(dump);

			logger.debug("Dump object in the communicationjsondump " + jsonDump.getFrom() + " :: "
					+ jsonDump.getReplyEmail() + " :: " + jsonDump.getFromName());

			toEmail = contactobj.getEmailId();

			Session sessionObj = getMessageProperties(channelAccount, channelSetting, userChannelSettingObj);

			MimeMessage message = createMessage(toEmail, jsonDump, "", finalContent, userChannelSettingObj,
					in, sessionObj);

			sendEmail(message, channelAccount, channelSetting, userChannelSettingObj);

		} catch (Exception e) {
			logger.info("Exception caused while json parsing : " + e);
		}

	}

	// prepare message and send
	// if any different fields needs to update then we get it from particular
	// dispatcher.

	protected Session getMessageProperties(ChannelAccount ocChannelAccount, ChannelSetting channelSetting,
			UserChannelSetting userChannelSetting) {
		logger.info("account user name : " + ocChannelAccount.getAccountName() + " PWD : "
				+ ocChannelAccount.getAccountPwd());
		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", channelSetting.getEndPoint());
		props.put("mail.smtp.port", channelSetting.getPort());
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.socketFactory.port", "465"); // take port from DB or default to 465
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.ssl.checkserveridentity", "true");// recommeneded by sonarlint
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		setAdditionalMsgProps(channelSetting, props);

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ocChannelAccount.getApiKey(), ocChannelAccount.getAccountPwd());
			}
		};
		return Session.getInstance(props, auth);

	}

	abstract void setAdditionalMsgProps(ChannelSetting channelSetting, Properties props);

	abstract MimeMessage prepareMessageHeader(MimeMessage messageObject, String commReportId, String sentId,
			String userId, String source, String toEmail);

	public void sendEmail(MimeMessage message, 
			ChannelAccount ocChannelAccount, ChannelSetting channelSetting, UserChannelSetting userChannelSetting
			) throws Exception {

		isConnected = checkConnection(ocChannelAccount, channelSetting, userChannelSetting);
		if (isConnected == false) {
			logger.info("transport is not connected");
			return;
		}

		try {
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (MessagingException me) {
			logger.error("Error while sending message" , me);
		}

	}

	private MimeMessage createMessage(String toEmail, CommunicationJSONDump jsonDump, String textBody, String htmlMsg,
			UserChannelSetting userChannelSetting, CustomCommunication in,
			Session sessionObj) throws MessagingException, UnsupportedEncodingException {
		subject = jsonDump.getSubject();
		fromEmail = jsonDump.getFrom();
		replyToEmail = jsonDump.getReplyEmail();
		fromName = jsonDump.getFromName();
		replyToName = jsonDump.getReplyToName();

		MimeMessage message = new MimeMessage(sessionObj);
		Multipart multipart = new MimeMultipart("alternative");

		message = prepareMessageHeader(message, in.getCommReportId()+"", "",
				userChannelSetting.getUserId().toString(), OCConstants.EMAIL_SOURCE_TYPE, toEmail);

		BodyPart part1 = new MimeBodyPart();
		part1.setText(textBody);

		BodyPart part2 = new MimeBodyPart();
		part2.setContent(htmlMsg, "text/html; charset=utf-8");

		multipart.addBodyPart(part1);
		multipart.addBodyPart(part2);

		message.setFrom(new InternetAddress(fromEmail, fromName));
		message.setReplyTo( new InternetAddress[] 
                {new InternetAddress(replyToEmail, replyToName)});
		message.addHeader("Return-Path", replyToEmail); // handle reply to
		message.setContent(multipart);
		message.setSubject(subject);
		if(replyToName != null) {
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, replyToName));
		}else {
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		}
		return message;
	}
// need to prepare and get communicationReport and communicationSent from Submitter class.

	// check connection
	public boolean checkConnection(ChannelAccount ocChannelAccount, ChannelSetting channelSetting,
			UserChannelSetting userChannelSetting) throws Exception {
		logger.error("trying to connect");
		try {
			Session sessionObj = getMessageProperties(ocChannelAccount, channelSetting, userChannelSetting);
			Provider provider = sessionObj.getProvider("smtp");
			transport = sessionObj.getTransport(provider);
			transport.connect(ocChannelAccount.getApiKey(), ocChannelAccount.getAccountPwd());

			isConnected = transport.isConnected();
		} catch (Exception e) {
			logger.info("Retrying by throwing :" + e);
			throw e;
		}
		return isConnected;
	}

//@Transactional
	@Recover
	private void addFailureContactsToDB(Exception e, Communication commObj, Contact contactobj,
			ChannelAccount channelAccount, ChannelSetting channelSetting, UserChannelSetting userChannelSettingObj,
			String finalContent, CommunicationSent sent) {
		// TODO might update the db rows with the cids and sent and crIds
		logger.info("Recovering failure.");

	}
}
