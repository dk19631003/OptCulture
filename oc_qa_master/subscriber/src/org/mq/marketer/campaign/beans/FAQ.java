package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class FAQ  {
	private Long userId;
	private String language;
	private String faqContent;
	//private String faqSpanishContent;
	private String termsAndCondition;
	//private String spanishTermsAndCondition;
	private Long faqId;
	private Calendar createdDate;
	private Long orgId;
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	/*public String getSpanishTermsAndCondition() {
		return spanishTermsAndCondition;
	}
	public void setSpanishTermsAndCondition(String spanishTermsAndCondition) {
		this.spanishTermsAndCondition = spanishTermsAndCondition;
	}
	
	public String getFaqSpanishContent() {
		return faqSpanishContent;
	}
	public void setFaqSpanishContent(String faqSpanishContent) {
		this.faqSpanishContent = faqSpanishContent;
	}*/
	
	public Long getFaqId() {
		return faqId;
	}
	public void setFaqId(Long faqId) {
		this.faqId = faqId;
	}
	public String getTermsAndCondition() {
		return termsAndCondition;
	}
	public void setTermsAndCondition(String termsAndCondition) {
		this.termsAndCondition = termsAndCondition;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFaqContent() {
		return faqContent;
	}
	public void setFaqContent(String faqContent) {
		this.faqContent = faqContent;
	}
	
}
