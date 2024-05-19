package org.mq.optculture.model.generalMailSender;

public class MailInfo {
	private String fromEmail;
	private String fromName;
	private String[] toEmail;
	private String[] ccEmail;
	private String[] bccEmail;
	private String subject;
	private String body;
	private byte[] attachment;
	private String fileName;
	private String extension;
	public MailInfo() {
	}
	
	

	public MailInfo(String fromEmail, String fromName, String[] toEmail,
			String[] ccEmail, String[] bccEmail, String subject, String body,
			byte[] attachment, String fileName, String extension) {
		super();
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.toEmail = toEmail;
		this.ccEmail = ccEmail;
		this.bccEmail = bccEmail;
		this.subject = subject;
		this.body = body;
		this.attachment = attachment;
		this.fileName = fileName;
		this.extension = extension;
	}



	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String[] getToEmail() {
		return toEmail;
	}
	public void setToEmail(String[] toEmail) {
		this.toEmail = toEmail;
	}
	public String[] getCcEmail() {
		return ccEmail;
	}
	public void setCcEmail(String[] ccEmail) {
		this.ccEmail = ccEmail;
	}
	public String[] getBccEmail() {
		return bccEmail;
	}
	public void setBccEmail(String[] bccEmail) {
		this.bccEmail = bccEmail;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public byte[] getAttachment() {
		return attachment;
	}
	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	//added for new json format, will be removed in future
	

	private String FROM_EMAIL;
	private String[] TO_EMAIL;
	private String SUBJECT;
	private String BODY;
	
	public MailInfo(String fROM_EMAIL, String[] tO_EMAIL, String sUBJECT,
			String bODY) {
		FROM_EMAIL = fROM_EMAIL;
		TO_EMAIL = tO_EMAIL;
		SUBJECT = sUBJECT;
		BODY = bODY;
	}
	public String getFROM_EMAIL() {
		return FROM_EMAIL;
	}
	public void setFROM_EMAIL(String fROM_EMAIL) {
		FROM_EMAIL = fROM_EMAIL;
	}
	public String[] getTO_EMAIL() {
		return TO_EMAIL;
	}
	public void setTO_EMAIL(String[] tO_EMAIL) {
		TO_EMAIL = tO_EMAIL;
	}
	public String getSUBJECT() {
		return SUBJECT;
	}
	public void setSUBJECT(String sUBJECT) {
		SUBJECT = sUBJECT;
	}
	public String getBODY() {
		return BODY;
	}
	public void setBODY(String bODY) {
		BODY = bODY;
	}

	
}
