package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import org.mq.optculture.utils.OCConstants;

public class FormMapping implements java.io.Serializable{
	private Long id;
	private Users users;
	private String URL;
	private String inputFieldMapping;
	private String formName;
	//private MailingList mailingList;
	private String htmlRedirectURL;
	private Boolean enable;
	private String submitFieldName;
	private String htmlRedirectFailureURL;
	private String htmlRedirectDbFailureURL;
	private String htmlRedirectParentalURL;
	
	String formType;
	Calendar activeSince;
	
	String formMappingName;
//	private Boolean isLoyaltyEnroll;
	private Long consentCustTemplateId;
	private boolean checkParentalEmail;
//	private Byte signupFormTypeSettings;
	
//	private Long loyaltyCustTemplateId;
//	private boolean checkLoyaltyEmail;
	
	private Long listId;
	
	private boolean sendEmailToExistingContact;
//	private Long custTemplateId;
//	private boolean checkAutoWelcomeEmail;
	
	private Long loyaltyProgramId;
	private Long loyaltyCardsetId;
	private char autoSelectCard;
	private char enableParentalConsent;
	private char checkFeedbackFormEmail;
	private char checkFeedbackFormSms;
	private Long feedBackMailCustTemplateId;
	private Long feedBackSmsTemplateId;
	private char issueRewardIschecked;
	private String issueRewardType;
	private String issueRewardValue;
	private char checkSimpleSignUpForEmail='N';
	private char checkSimpleSignUpFormSms='N';
	private Long simpleSignUpCustTemplateId;
	private Long simpleSignUpSmsTemplateId;
	private char doIssuePoints = 'N';
	
   //webhook for survey Typeform
	private boolean webHook;
	


	public boolean isWebHook() {
		return webHook;
	}

	public void setWebHook(boolean webHook) {
		this.webHook = webHook;
	}

	public boolean isSendEmailToExistingContact() {
		return sendEmailToExistingContact;
	}

	public void setSendEmailToExistingContact(boolean sendEmailToExistingContact) {
		this.sendEmailToExistingContact = sendEmailToExistingContact;
	}

	public String getHtmlRedirectParentalURL() {
		return htmlRedirectParentalURL;
	}

	public void setHtmlRedirectParentalURL(String htmlRedirectParentalURL) {
		this.htmlRedirectParentalURL = htmlRedirectParentalURL;
	}

	public String getHtmlRedirectDbFailureURL() {
		return htmlRedirectDbFailureURL;
	}

	public void setHtmlRedirectDbFailureURL(String htmlRedirectDbFailureURL) {
		this.htmlRedirectDbFailureURL = htmlRedirectDbFailureURL;
	}

	
	
	
	/*public boolean isCheckAutoWelcomeEmail() {
		return checkAutoWelcomeEmail;
	}

	public void setCheckAutoWelcomeEmail(boolean checkAutoWelcomeEmail) {
		this.checkAutoWelcomeEmail = checkAutoWelcomeEmail;
	}

	public Long getCustTemplateId() {
		return custTemplateId;
	}

	public void setCustTemplateId(Long custTemplateId) {
		this.custTemplateId = custTemplateId;
	}*/

	public FormMapping() {
	}
	
	public FormMapping(Users users, String URL, String inputFieldString, String formName, long  listId) {
		this.users = users;
		this.URL = URL;
		this.inputFieldMapping = inputFieldString;
		this.formName = formName;
		this.listId = listId;
		this.enable = true;
	}

	public FormMapping(Users users, String URL, String formMappingName, Calendar activeSince,char checkFeedbackFormEmail,char checkFeedbackFormSms,char issueRewardIschecked) {
		this.users = users;
		this.URL = URL;
		this.formMappingName= formMappingName;
		this.activeSince = activeSince;
		this.enable = true;
 		this.checkFeedbackFormEmail = OCConstants.FLAG_NO;
		this.checkFeedbackFormSms = OCConstants.FLAG_NO;
		this.issueRewardIschecked = OCConstants.FLAG_NO;	
	}
	
	
	
	public String getSubmitFieldName() {
		return submitFieldName;
	}

	public void setSubmitFieldName(String submitFieldName) {
		this.submitFieldName = submitFieldName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public String getInputFieldMapping() {
		return inputFieldMapping;
	}

	public void setInputFieldMapping(String inputFieldMapping) {
		this.inputFieldMapping = inputFieldMapping;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
/*
	public MailingList getMailingList() {
		return mailingList;
	}

	public void setMailingList(MailingList mailingList) {
		this.mailingList = mailingList;
	}*/

	public Boolean isEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	
	public String getHtmlRedirectURL() {
		return htmlRedirectURL;
	}

	public void setHtmlRedirectURL(String htmlRedirectURL) {
		this.htmlRedirectURL = htmlRedirectURL;
	}

	public String getHtmlRedirectFailureURL() {
		return htmlRedirectFailureURL;
	}

	public void setHtmlRedirectFailureURL(String htmlRedirectFailureURL) {
		this.htmlRedirectFailureURL = htmlRedirectFailureURL;
	}

	
	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Calendar getActiveSince() {
		return activeSince;
	}

	public void setActiveSince(Calendar activeSince) {
		this.activeSince = activeSince;
	}

	public String getFormMappingName() {
		return formMappingName;
	}

	public void setFormMappingName(String formMappingName) {
		this.formMappingName = formMappingName;
	}

	/*public Boolean getIsLoyaltyEnroll() {
		return isLoyaltyEnroll;
	}

	public void setIsLoyaltyEnroll(Boolean isLoyaltyEnroll) {
		this.isLoyaltyEnroll = isLoyaltyEnroll;
	}*/

	public Long getConsentCustTemplateId() {
		return consentCustTemplateId;
	}

	public void setConsentCustTemplateId(Long consentCustTemplateId) {
		this.consentCustTemplateId = consentCustTemplateId;
	}

	public boolean isCheckParentalEmail() {
		return checkParentalEmail;
	}

	public void setCheckParentalEmail(boolean checkParentalEmail) {
		this.checkParentalEmail = checkParentalEmail;
	}

	/*public Byte getSignupFormTypeSettings() {
		return signupFormTypeSettings;
	}

	public void setSignupFormTypeSettings(Byte signupFormTypeSettings) {
		this.signupFormTypeSettings = signupFormTypeSettings;
	}*/

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	/*public Long getLoyaltyCustTemplateId() {
		return loyaltyCustTemplateId;
	}

	public void setLoyaltyCustTemplateId(Long loyaltyCustTemplateId) {
		this.loyaltyCustTemplateId = loyaltyCustTemplateId;
	}

	public boolean isCheckLoyaltyEmail() {
		return checkLoyaltyEmail;
	}

	public void setCheckLoyaltyEmail(boolean checkLoyaltyEmail) {
		this.checkLoyaltyEmail = checkLoyaltyEmail;
	}*/

	public Long getLoyaltyProgramId() {
		return loyaltyProgramId;
	}

	public void setLoyaltyProgramId(Long loyaltyProgramId) {
		this.loyaltyProgramId = loyaltyProgramId;
	}

	public Long getLoyaltyCardsetId() {
		return loyaltyCardsetId;
	}

	public void setLoyaltyCardsetId(Long loyaltyCardsetId) {
		this.loyaltyCardsetId = loyaltyCardsetId;
	}

	public char getAutoSelectCard() {
		return autoSelectCard;
	}

	public void setAutoSelectCard(char autoSelectCard) {
		this.autoSelectCard = autoSelectCard;
	}

	public char getEnableParentalConsent() {
		return enableParentalConsent;
	}

	public void setEnableParentalConsent(char enableParentalConsent) {
		this.enableParentalConsent = enableParentalConsent;
	}

	public char getCheckFeedbackFormEmail() {
		return checkFeedbackFormEmail;
	}

	public void setCheckFeedbackFormEmail(char checkFeedbackFormEmail) {
		this.checkFeedbackFormEmail = checkFeedbackFormEmail;
	}

	public char getCheckFeedbackFormSms() {
		return checkFeedbackFormSms;
	}

	public void setCheckFeedbackFormSms(char checkFeedbackFormSms) {
		this.checkFeedbackFormSms = checkFeedbackFormSms;
	}

	public Long getFeedBackMailCustTemplateId() {
		return feedBackMailCustTemplateId;
	}

	public void setFeedBackMailCustTemplateId(Long feedBackMailCustTemplateId) {
		this.feedBackMailCustTemplateId = feedBackMailCustTemplateId;
	}

	public Long getFeedBackSmsTemplateId() {
		return feedBackSmsTemplateId;
	}

	public void setFeedBackSmsTemplateId(Long feedBackSmsTemplateId) {
		this.feedBackSmsTemplateId = feedBackSmsTemplateId;
	}

	public String getIssueRewardType() {
		return issueRewardType;
	}

	public void setIssueRewardType(String issueRewardType) {
		this.issueRewardType = issueRewardType;
	}

	public String getIssueRewardValue() {
		return issueRewardValue;
	}

	public void setIssueRewardValue(String issueRewardValue) {
		this.issueRewardValue = issueRewardValue;
	}

	public char getIssueRewardIschecked() {
		return issueRewardIschecked;
	}

	public void setIssueRewardIschecked(char issueRewardIschecked) {
		this.issueRewardIschecked = issueRewardIschecked;
	}

	public char getCheckSimpleSignUpForEmail() {
		return checkSimpleSignUpForEmail;
	}

	public void setCheckSimpleSignUpForEmail(char checkSimpleSignUpForEmail) {
		this.checkSimpleSignUpForEmail = checkSimpleSignUpForEmail;
	}

	public char getCheckSimpleSignUpFormSms() {
		return checkSimpleSignUpFormSms;
	}

	public void setCheckSimpleSignUpFormSms(char checkSimpleSignUpFormSms) {
		this.checkSimpleSignUpFormSms = checkSimpleSignUpFormSms;
	}

	public Long getSimpleSignUpCustTemplateId() {
		return simpleSignUpCustTemplateId;
	}

	public void setSimpleSignUpCustTemplateId(Long simpleSignUpCustTemplateId) {
		this.simpleSignUpCustTemplateId = simpleSignUpCustTemplateId;
	}

	public Long getSimpleSignUpSmsTemplateId() {
		return simpleSignUpSmsTemplateId;
	}

	public void setSimpleSignUpSmsTemplateId(Long simpleSignUpSmsTemplateId) {
		this.simpleSignUpSmsTemplateId = simpleSignUpSmsTemplateId;
	}

	public char getDoIssuePoints() {
		return doIssuePoints;
	}

	public void setDoIssuePoints(char doIssuePoints) {
		this.doIssuePoints = doIssuePoints;
	}
	
}
