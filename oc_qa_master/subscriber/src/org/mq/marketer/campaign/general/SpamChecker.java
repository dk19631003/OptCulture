package org.mq.marketer.campaign.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SpamChecker {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private JavaMailSenderImpl sender;
	private String doSpamCheck = null;
	
	
	private final StringBuffer emailContent = new StringBuffer("Return-Path: <support@captiway.com>\r\n" +
			"X-Original-To: support@captiway.com\r\nDelivered-To: support@captiway.com\r\n" +
			"Received: from o1.email.mailsender01.info (o1.email.mailsender01.info [167.89.20.150])\r\n" +
			"	by magnaquest.net (Postfix) with with SMTP id A999982A017\r\n" +
			
			"	for <support@captiway.com>; Thu,  8 Apr 2010 11:26:56 +0000 (UTC)\r\n" +
			"X-Virus-Scanned: amavisd-new at localhost\r\nReceived: from magnaquest.net ([127.0.0.1])\r\n" +
			"	by localhost (magnaquest.net [127.0.0.1]) (amavisd-new, port 10024)\r\n" +
			"	with ESMTP id Ng8El42G0r8S for <support@captiway.com>;\r\n" +
			"	Thu,  8 Apr 2010 16:56:53 +0530 (IST)\r\n" +
			"Received: from cwsend01.info (cwsend01.info [123.176.39.101])\r\n" +
			"	by magnaquest.com (Postfix) with ESMTP id 6BD5C82A023\r\n" +
			"	for <support@captiway.com>; Thu,  8 Apr 2010 16:56:34 +0530 (IST)\r\n" +
			//"DKIM-Signature: v=1; a=rsa-sha1; c=relaxed/relaxed; s=key1; d=cwsend01.info;\r\n" +
			" h=From:To:Subject:MIME-Version:Content-Type:Date;\r\n" +
			" bh=NOiSAlswEOVuwZVUVxfxNftqd0w=;\r\n" +
			" b=TsM2An1M31Z1jJ1M6rEmVPu2LbOkkI1QI1zWqjSWNLqwZobVQIlkjhh2XiHFtPkrBvmoXSl2oHRE\r\n" +
			"  cXjUP2LRaw==\r\n" +
			"Received: by cwsend01.info (PowerMTA(TM) v4.0r1) id hnhq5216vicc for <support@captiway.com>;" +
			" Wed, 23 May 2012 10:52:31 +0530 (envelope-from <support@captiway.com>)\r\n" +
			"Date: Thu, 8 Apr 2010 06:27:26 -0500 (CDT)\r\n" +
			"From: captiway support <support@captiway.com>\r\n" +
			"Reply-To: support@captiway.com\r\nTo: support@captiway.com\r\n" +
			"Message-ID: <383097543.17.1337573989684.JavaMail.magna@testserver>\r\n" +
			"Subject: |^subject^|\r\n" +
			"MIME-Version: 1.0\r\nContent-Type: multipart/alternative; \r\n" +
			"	boundary=\"----=_Part_48677_1036863371.1270726046907\"\r\n\r\n" +
			"------=_Part_48677_1036863371.1270726046907\r\nContent-Type: text/plain; \r\n" +
			"	boundary=\"----=_Part_48678_1880364639.1270726046907\"\r\n\r\n" +
			"------=_Part_48678_1880364639.1270726046907\r\n" +
			"Content-Type: text/html; charset=UTF-8\r\n" +
			"Content-Transfer-Encoding: quoted-printable\r\n\r\n" +
			"<html><body><div> |^bodyContent^| </div></body></html>" +
			"\r\n\r\n------=_Part_48678_1880364639.1270726046907--\r\n\r\n" +
			"------=_Part_48677_1036863371.1270726046907--"); 
	
	
	private StringBuffer textContent = new StringBuffer("Received: from cwsend01.info\r\n" +
			"Message-Id: <msgId@cwsend01.info>\r\n" +
			"Date: Fri, 20 Apr 2010 00:00:59 -0400\r\nTo: support@captiway.com\r\n" +
			"Subject: |^subject^|\r\n\r\n|^bodyContent^|");


	public SpamChecker() {
		sender = new JavaMailSenderImpl();
	    sender.setHost(PropertyUtil.getPropertyValue("SMTPHost"));
	    doSpamCheck = PropertyUtil.getPropertyValue("doSpamCheck");
	}
	

	public StringBuffer checkSpam(String sub,String text, String filePath, boolean isHTML) {
		logger.debug("Trying to check the Spam");
		
		if(doSpamCheck==null || !doSpamCheck.equals("true")) {
			return null;
		}
		
		StringBuffer response = new StringBuffer();
		Process proc = null;
		File emlFile = new File(filePath);
		try {
			
			boolean res = createEmlFile( isHTML, sub, text, emlFile);
			//logger.debug("Result : " +res);
			File spamScriptFile =  new File(PropertyUtil.getPropertyValue("spamScriptFile"));
			
			if(!spamScriptFile.canExecute()) {
				if(!spamScriptFile.setExecutable(true)) {
					logger.warn("Unable to provide execute permission to spam checker script");
					return null;
				}
			}
			
			if(res){
			//String path = "/venkatesh/test/message.eml";
			String command = PropertyUtil.getPropertyValue("spamScriptFile") + " " + filePath;
			
			//String command = "spamc -c < " + path;
			logger.debug("Command to execute : " + command);
			proc = Runtime.getRuntime().exec(command);
				if(proc !=null){
					InputStream is = proc.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String output = "";
					while((output= br.readLine()) != null){
						response.append(output + "\r\n");
/*						if(output.contains("/")){
							logger.debug("Output : " + output);
							break;
						}
*/					}
					br.close();
					isr.close();
					return response;
				}else{
					logger.debug("Process was null");
				}
			}
		} catch (Exception e) {
			logger.error("** Exception : problem while checking the spam - " + e);
		}
		finally {
			if(emlFile.exists())emlFile.delete();
		}
		return response;
	}
	
	public boolean createEmlFile(boolean isHTML, String sub, String text, File emlFile) {
		try {
			StringBuffer template = ((isHTML)?emailContent:textContent);
			
			String tempContent = template.toString().replace("|^subject^|", sub);
			tempContent = tempContent.replace("|^bodyContent^|", text);
			//logger.debug("Eml Content :" + tempContent);
			
			BufferedWriter bos = new BufferedWriter(new FileWriter(emlFile));
			bos.write(tempContent);
			bos.flush();
			bos.close();
		
			if (!emlFile.exists()) {
				logger.debug("EML file is not created exist");
				return false;
			}
			
			return true;

		} catch (Exception e) {
			logger.error("** Exception : Problem while creating the EML file **" );
			return false;
		}
	}
}
