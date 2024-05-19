package org.mq.captiway.scheduler.beans;

import java.util.HashMap;

@SuppressWarnings({ "unchecked", "serial" })
public class Email {
    private String subject;
    private String from;
    private String to;
    private String reply;
    private String htmlText;
    private String plainTextMsg;
    private HashMap imageMap;
    private String mailSmtpFrom;
    private boolean multipart = true;
    private boolean html;
    private String emailType;
	
    
    
    public Email() {
	}

	public Email(String subject, String from, String reply) {
		this.subject = subject;
		this.from = from;
		this.reply = reply;
	}

	public Email(String subject, String from, String to, String reply,
			String htmlText, String plainTextMsg, HashMap imageMap,
			String mailSmtpFrom, boolean multipart, boolean html,
			String emailType) {
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.reply = reply;
		this.htmlText = htmlText;
		this.plainTextMsg = plainTextMsg;
		this.imageMap = imageMap;
		this.mailSmtpFrom = mailSmtpFrom;
		this.multipart = multipart;
		this.html = html;
		this.emailType = emailType;
	}
    
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getHtmlText() {
		return htmlText;
	}
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	public String getPlainTextMsg() {
		return plainTextMsg;
	}
	public void setPlainTextMsg(String plainTextMsg) {
		this.plainTextMsg = plainTextMsg;
	}
	public HashMap getImageMap() {
		return imageMap;
	}
	public void setImageMap(HashMap imageMap) {
		this.imageMap = imageMap;
	}
	public String getMailSmtpFrom() {
		return mailSmtpFrom;
	}
	public void setMailSmtpFrom(String mailSmtpFrom) {
		this.mailSmtpFrom = mailSmtpFrom;
	}
	public boolean isMultipart() {
		return multipart;
	}
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}
	public boolean isHtml() {
		return html;
	}
	public void setHtml(boolean html) {
		this.html = html;
	}
	public String getEmailType() {
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
    
    
}
