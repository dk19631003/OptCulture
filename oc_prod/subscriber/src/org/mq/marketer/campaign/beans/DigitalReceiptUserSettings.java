package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class DigitalReceiptUserSettings implements java.io.Serializable {
	
	private Long id;
	private Long userId;
	private String subject;
	private String fromEmail;
	private String selectedTemplateName;
	private String templateJsonSettings;
	private Calendar createdDate;

	private boolean webLinkFlag;
	private boolean personalizeTo;
	private String toName;
	private String webLinkText;
	private String webLinkUrlText;
	private boolean permissionRemainderFlag;
	private String permissionRemainderText;
	
	private boolean enabled;
	private boolean smsEnabled = false;
	private String messageContent;
	
	private boolean includeTax;
	private boolean includeFee;
	private boolean includeShipping;
	private boolean includeGlobalDiscount;
	private boolean includeTotalAmount;
	

	private Long myTemplateId;
	private Long zoneId;
	

	private boolean settingEnable=true;

	private	boolean includeDynamicReplyToEmail;
	private String replyToEmail;
	
	private boolean creditNoteEnabled;	//APP-4189
	private Long CNTemplateId; // credit note template

	public boolean isCreditNoteEnabled() {
		return creditNoteEnabled;
	}

	public void setCreditNoteEnabled(boolean creditNoteEnabled) {
		this.creditNoteEnabled = creditNoteEnabled;
	}
	
	public Long getCNTemplateId() {
		return CNTemplateId;
	}

	public void setCNTemplateId(Long cNTemplateId) {
		CNTemplateId = cNTemplateId;
	}


	//3706
	private String dateFormat;
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public boolean isIncludeDynamicReplyToEmail() {
		return includeDynamicReplyToEmail;
	}

	public void setIncludeDynamicReplyToEmail(boolean includeDynamicReplyToEmail) {
		this.includeDynamicReplyToEmail = includeDynamicReplyToEmail;
	}

	
	public String getReplyToEmail() {
		return replyToEmail;
	}

	public void setReplyToEmail(String replyToEmail) {
		this.replyToEmail = replyToEmail;
	}

	
	public boolean isSettingEnable() {
		return settingEnable;
	}

	public void setSettingEnable(boolean settingEnable) {
		this.settingEnable = settingEnable;
	}

	public Long getMyTemplateId() {
		return myTemplateId;
	}

	public void setMyTemplateId(Long myTemplateId) {
		this.myTemplateId = myTemplateId;
	}
	
	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	
	public boolean isIncludeTotalAmount() {
		return includeTotalAmount;
	}

	public void setIncludeTotalAmount(boolean includeTotalAmount) {
		this.includeTotalAmount = includeTotalAmount;
	}


	private boolean includeDynamicFrmName;
	private boolean includeDynamicFrmEmail;
	
	public boolean isIncludeDynamicFrmName() {
		return includeDynamicFrmName;
	}

	public void setIncludeDynamicFrmName(boolean includeDynamicFrmName) {
		this.includeDynamicFrmName = includeDynamicFrmName;
	}

	public boolean isIncludeDynamicFrmEmail() {
		return includeDynamicFrmEmail;
	}

	public void setIncludeDynamicFrmEmail(boolean includeDynamicFrmEmail) {
		this.includeDynamicFrmEmail = includeDynamicFrmEmail;
	}


	
	
	
	
	public boolean isIncludeTax() {
		return includeTax;
	}

	public void setIncludeTax(boolean includeTax) {
		this.includeTax = includeTax;
	}

	public boolean isIncludeFee() {
		return includeFee;
	}

	public void setIncludeFee(boolean includeFee) {
		this.includeFee = includeFee;
	}

	public boolean isIncludeShipping() {
		return includeShipping;
	}

	public void setIncludeShipping(boolean includeShipping) {
		this.includeShipping = includeShipping;
	}


	
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public DigitalReceiptUserSettings(){
		
	}
	
    public boolean isWebLinkFlag() {
		return webLinkFlag;
	}

	public void setWebLinkFlag(boolean webLinkFlag) {
		this.webLinkFlag = webLinkFlag;
	}

	public String getWebLinkText() {
		return webLinkText;
	}

	public void setWebLinkText(String webLinkText) {
		this.webLinkText = webLinkText;
	}

	public String getWebLinkUrlText() {
		return webLinkUrlText;
	}

	public void setWebLinkUrlText(String webLinkUrlText) {
		this.webLinkUrlText = webLinkUrlText;
	}

	public boolean isPermissionRemainderFlag() {
		return permissionRemainderFlag;
	}

	public void setPermissionRemainderFlag(boolean permissionRemainderFlag) {
		this.permissionRemainderFlag = permissionRemainderFlag;
	}

	public String getPermissionRemainderText() {
		return permissionRemainderText;
	}

	public void setPermissionRemainderText(String permissionRemainderText) {
		this.permissionRemainderText = permissionRemainderText;
	}
    
	
	public boolean isIncludeGlobalDiscount() {
		return includeGlobalDiscount;
	}

	public void setIncludeGlobalDiscount(boolean includeGlobalDiscount) {
		this.includeGlobalDiscount = includeGlobalDiscount;
	}


	private String fromName;
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSelectedTemplateName() {
		return selectedTemplateName;
	}

	public void setSelectedTemplateName(String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}

	public String getTemplateJsonSettings() {
		return templateJsonSettings;
	}

	public void setTemplateJsonSettings(String templateJsonSettings) {
		this.templateJsonSettings = templateJsonSettings;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	 public boolean isPersonalizeTo() {
		return personalizeTo;
	}

	public void setPersonalizeTo(boolean personalizeTo) {
		this.personalizeTo = personalizeTo;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public boolean isSmsEnabled() {
		return smsEnabled;
	}

	public void setSmsEnabled(boolean smsEnabled) {
		this.smsEnabled = smsEnabled;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
}
